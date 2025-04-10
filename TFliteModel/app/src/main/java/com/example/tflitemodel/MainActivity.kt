package com.example.tflitemodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.example.tflitemodel.ui.theme.TFliteModelTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.tensorflow.lite.Interpreter
import java.io.File
import java.io.FileInputStream
import java.nio.MappedByteBuffer
import java.nio.channels.FileChannel
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.round

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            TFliteModelTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    content = { padding ->
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(padding)
                        ) {
                            TFLiteDemoScreenWithImprovement(context = this@MainActivity)
                        }
                    }
                )
            }
        }
    }
}

fun loadModelFile(context: Context, modelPath: String): MappedByteBuffer {
    val assetFileDescriptor = context.assets.openFd(modelPath)
    val inputStream = FileInputStream(assetFileDescriptor.fileDescriptor)
    val fileChannel = inputStream.channel
    val startOffset = assetFileDescriptor.startOffset
    val declaredLength = assetFileDescriptor.declaredLength
    return fileChannel.map(FileChannel.MapMode.READ_ONLY, startOffset, declaredLength)
}

fun preprocessImageTo4D(bitmap: Bitmap): Array<Array<Array<FloatArray>>> {
    val imageSize = 640
    val paddedBitmap = resizeWithAspectRatioAndPadding(bitmap) // アスペクト比維持＆余白埋め

    val outputArray = Array(1) { Array(imageSize) { Array(imageSize) { FloatArray(3) } } }

    for (y in 0 until imageSize) {
        for (x in 0 until imageSize) {
            val pixel = paddedBitmap.getPixel(x, y)
            outputArray[0][y][x][0] = ((pixel shr 16) and 0xFF) / 255.0f
            outputArray[0][y][x][1] = ((pixel shr 8) and 0xFF) / 255.0f
            outputArray[0][y][x][2] = (pixel and 0xFF) / 255.0f
        }
    }
    return outputArray
}

fun resizeWithAspectRatioAndPadding(bitmap: Bitmap): Bitmap {
    val imageSize = 640
    val aspectRatio = bitmap.width.toFloat() / bitmap.height
    val targetWidth: Int
    val targetHeight: Int

    // アスペクト比を考慮したリサイズ
    if (aspectRatio > 1) {
        targetWidth = imageSize
        targetHeight = (imageSize / aspectRatio).toInt()
    } else {
        targetWidth = (imageSize * aspectRatio).toInt()
        targetHeight = imageSize
    }

    val resizedBitmap = Bitmap.createScaledBitmap(bitmap, targetWidth, targetHeight, true)

    // 空白部分を埋めるための新しいBitmapを作成
    val paddedBitmap = Bitmap.createBitmap(imageSize, imageSize, Bitmap.Config.ARGB_8888)
    val canvas = android.graphics.Canvas(paddedBitmap)

    // 背景色を設定（ここでは緑色）
    canvas.drawColor(android.graphics.Color.GREEN)

    // 中央にリサイズした画像を配置
    canvas.drawBitmap(
        resizedBitmap,
        ((imageSize - targetWidth) / 2).toFloat(),
        ((imageSize - targetHeight) / 2).toFloat(),
        null
    )

    return paddedBitmap
}

fun parseMahjongDetections(resultArray: Array<Array<FloatArray>>, confThreshold: Float = 0.5f): Map<String, MutableList<Float>> {
    val numAnchors = 8400
    val numClasses = 37
    val tileLabels = listOf(
        "1m", "1p", "1s", "2m", "2p", "2s", "3m", "3p", "3s",
        "4m", "4p", "4s", "5m", "5p", "5s", "6m", "6p", "6s",
        "7m", "7p", "7s", "8m", "8p", "8s", "9m", "9p", "9s",
        "D5m", "D5p", "D5s", "haku", "hatu", "higashi", "kita", "minami", "nishi", "tyun"
    )

    val objIndex = 0
    val classScoreStart = 4
    val tileMap = mutableMapOf<String, MutableList<Float>>()

    for (i in 0 until numAnchors) {
        val objectness = resultArray[0][objIndex][i]
        if (objectness < confThreshold) continue

        var bestClassIndex = -1
        var bestClassScore = Float.MIN_VALUE
        for (cls in 0 until numClasses) {
            val score = resultArray[0][classScoreStart + cls][i]
            if (score > bestClassScore) {
                bestClassScore = score
                bestClassIndex = cls
            }
        }

        if (bestClassIndex in tileLabels.indices && bestClassScore > confThreshold) {
            val tileName = tileLabels[bestClassIndex]
            tileMap.getOrPut(tileName) { mutableListOf() }.add(bestClassScore)
            Log.d("DETECTION", "Tile: $tileName, Confidence: $bestClassScore")
        }
    }
    return tileMap
}

/*
 * countDetectionsでは、各タイルの検出数（confThreshold以上の信頼度の数）を元に、
 * 四捨五入して10で割った商を最終的な検出個数としています。
 * これにより、例えばrawな検出数が30件なら3個、10件なら1個と推定できます。
 * また、rawな検出数が1～9件の場合は最低1個とします。
 */
fun countDetections(tileMap: Map<String, MutableList<Float>>, confThreshold: Float = 0.5f): Map<String, Int> {
    return tileMap.mapValues { (_, confidences) ->
        val rawCount = confidences.count { it >= confThreshold }
        // 四捨五入して10で割った商を求める
        val estimatedCount = round(rawCount / 10.0).toInt()
        if (estimatedCount < 1) 1 else estimatedCount
    }
}

fun runTFLiteInferenceWithImprovement(context: Context, inputImage: Bitmap, confThreshold: Float = 0.5f): Map<String, Int> {
    val interpreter = Interpreter(loadModelFile(context, "model.tflite"))
    val inputArray = preprocessImageTo4D(inputImage)
    val outputArray = Array(1) { Array(41) { FloatArray(8400) } }
    interpreter.run(inputArray, outputArray)
    interpreter.close()

    val tileMap = parseMahjongDetections(outputArray, confThreshold)
    return countDetections(tileMap, confThreshold)
}

/*
 * 高解像度の画像を取得するために、一時ファイルを作成しFileProvider経由でカメラに渡します。
 * ※FileProviderの設定はAndroidManifest.xml側で行ってください。
 */
fun createImageFile(context: Context): File {
    val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
    val fileName = "JPEG_${timeStamp}_"
    // キャッシュディレクトリに一時ファイルを作成
    return File.createTempFile(fileName, ".jpg", context.cacheDir)
}

@Composable
fun TFLiteDemoScreenWithImprovement(context: Context) {
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var resultText by remember { mutableStateOf("モデルの出力結果がここに表示されます") }
    // ギャラリーから画像選択するランチャー
    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            context.contentResolver.openInputStream(it)?.use { stream ->
                selectedBitmap = BitmapFactory.decodeStream(stream)
            }
        }
    }
    // カメラ撮影用の一時ファイルを保持するための状態
    var currentPhotoFile by remember { mutableStateOf<File?>(null) }
    // カメラで撮影するランチャー（TakePicture：高解像度画像が得られます）
    val cameraLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success) {
            currentPhotoFile?.let { file ->
                // 撮影した画像ファイルからBitmapを生成
                selectedBitmap = BitmapFactory.decodeFile(file.absolutePath)
            }
        }
    }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        BasicText(text = resultText, modifier = Modifier.padding(bottom = 16.dp))

        selectedBitmap?.let {
            Image(
                bitmap = it.asImageBitmap(),
                contentDescription = "Selected Image",
                modifier = Modifier.size(240.dp).padding(bottom = 16.dp)
            )
        } ?: BasicText("画像が選択されていません", modifier = Modifier.padding(bottom = 16.dp))

        // ギャラリーから画像選択ボタン
        Button(onClick = { galleryLauncher.launch("image/*") }) {
            BasicText("画像を選択")
        }
        Spacer(modifier = Modifier.height(8.dp))
        // カメラで撮影ボタン
        Button(onClick = {
            // 高解像度の画像を得るために、一時ファイルを作成してFileProviderからURIを取得
            val file = createImageFile(context)
            currentPhotoFile = file
            val uri = FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
            cameraLauncher.launch(uri)
        }) {
            BasicText("カメラ撮影")
        }
        Spacer(modifier = Modifier.height(16.dp))
        // モデル実行ボタン
        Button(
            onClick = {
                selectedBitmap?.let { bitmap ->
                    resultText = "処理中..."
                    CoroutineScope(Dispatchers.Default).launch {
                        val results = runTFLiteInferenceWithImprovement(context, bitmap)
                        withContext(Dispatchers.Main) {
                            resultText = if (results.isNotEmpty()) {
                                results.entries.joinToString(", ") { "${it.key}: ${it.value}個" }
                            } else {
                                "検出されませんでした"
                            }
                        }
                    }
                } ?: run {
                    resultText = "先に画像を選択または撮影してください"
                }
            },
            enabled = (selectedBitmap != null)
        ) {
            BasicText("モデル実行（改善版）")
        }
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewTFLiteDemoScreen() {
    TFliteModelTheme {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            BasicText("プレビュー表示用")
        }
    }
}
