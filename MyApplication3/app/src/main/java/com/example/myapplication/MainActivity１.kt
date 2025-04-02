//package com.example.myapplication
//
//import android.os.Bundle
//import androidx.activity.enableEdgeToEdge
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.view.ViewCompat
//import androidx.core.view.WindowInsetsCompat
//import com.chaquo.python.Python
//
//class MainActivity１ : AppCompatActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
//        setContentView(R.layout.activity_main)
//
////        // インスタンスを取得
////        val py = Python.getInstance()
////        // src/main/python/hello.pyを指定する
////        val module = py.getModule("hello")
////        // hello.pyのhello_world関数を呼ぶ
////        val txt1 = module.callAttr("hello_world")
////        // hello.pyのset_text関数を呼ぶ。関数の引数も指定できる
////        val txt2 = module.callAttr("set_text", "Good Morning")
////        // logに出力。Logcatに出力される。
////        println(txt1)
////        println(txt2)
////
////        // ------ 以下の記述を追加 ------
////        val num1 = module.callAttr("test_numpy")
////        //val num2 = module.callAttr("test_pandas")
////        println(num1)
////        //println(num2)
//
//
//        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
//            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
//            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
//            insets
//        }
//    }
//}