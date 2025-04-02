//package com.example.myapplication
//
//import android.os.Bundle
//import android.util.Log
//import androidx.appcompat.app.AppCompatActivity
//import com.chaquo.python.Python
//import com.chaquo.python.android.AndroidPlatform
//
//class MainActivity : AppCompatActivity() {
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Chaquopyを初期化
//        if (!Python.isStarted()) {
//            Python.start(AndroidPlatform(this))
//        }
//
//        val py = Python.getInstance()
//
//        // Pythonファイルを実行する
//        try {
//            // `mahjong_hand.py`モジュールをインポート
//            val mahjongModule = py.getModule("mahjong_hand") // "mahjong_hand.py"を指す
//
//            // `result_data`変数を取得
//            val resultData = mahjongModule.get("result_data") // Python側で計算済みの結果を取得
//
//            // 結果をLogcatに出力
//            Log.d("MahjongResult", resultData.toString())
//
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//    }
//}

package com.example.myapplication

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform
import org.json.JSONObject

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Chaquopyを初期化
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
        val py = Python.getInstance()

        // UI要素の取得
        val manInput = findViewById<EditText>(R.id.editMan)
        val pinInput = findViewById<EditText>(R.id.editPin)
        val souInput = findViewById<EditText>(R.id.editSou)
        val honorsInput = findViewById<EditText>(R.id.editHonors)

        val winTileInput = findViewById<EditText>(R.id.editWinTile) // 上がり牌
        val doraInput = findViewById<EditText>(R.id.editDora)      // ドラ表示牌

        val resultView = findViewById<TextView>(R.id.resultTextView)
        val calcButton = findViewById<Button>(R.id.calcButton)

        // 計算ボタンのクリック処理
        calcButton.setOnClickListener {
            try {
                // 入力データを収集
                val man = manInput.text.toString()
                val pin = pinInput.text.toString()
                val sou = souInput.text.toString()
                val honors = honorsInput.text.toString()

                val winTile = winTileInput.text.toString()
                val dora = doraInput.text.toString()

                // 上がり牌を解析
                val winTileData = parseTile(winTile)
                if (winTileData == null) {
                    resultView.text = "上がり牌の形式が正しくありません。例: man:1, honors:5"
                    return@setOnClickListener
                }

                // ドラ表示牌を解析
                val doraData = parseTile(dora)
                if (dora.isNotEmpty() && doraData == null) {
                    resultView.text = "ドラ表示牌の形式が正しくありません。例: pin:7, sou:9"
                    return@setOnClickListener
                }

                // 入力データをJSON形式でPythonに渡す
                val inputData = JSONObject()
                inputData.put("man", man)
                inputData.put("pin", pin)
                inputData.put("sou", sou)
                inputData.put("honors", honors)

                inputData.put("win_tile", winTileData)
                inputData.put("dora", doraData)

                // Pythonモジュールを呼び出し
                val module = py.getModule("mahjong_test1")
                val result = module.callAttr("calculate_score", inputData.toString())

                // 結果を表示
                resultView.text = result.toString()
            } catch (e: Exception) {
                e.printStackTrace()
                resultView.text = "エラー: ${e.message}"
            }
        }
    }

    // 牌の形式を解析する関数 (例: "man:1" -> { "type": "man", "value": 1 })
    private fun parseTile(tile: String): JSONObject? {
        val parts = tile.split(":")
        if (parts.size == 2) {
            val type = parts[0].trim()
            val value = parts[1].trim().toIntOrNull()
            if (value != null && (type == "man" || type == "pin" || type == "sou" || type == "honors")) {
                return JSONObject().apply {
                    put("type", type)
                    put("value", value)
                }
            }
        }
        return null
    }
}


