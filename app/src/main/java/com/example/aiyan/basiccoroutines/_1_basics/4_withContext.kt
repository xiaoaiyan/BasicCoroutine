package com.example.aiyan.basiccoroutines._1_basics

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.aiyan.basiccoroutines.thread
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


class WithContextActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        CoroutineScope(Dispatchers.Main).launch {
            println("1 - Main launch : ${thread()}")

            /*
            开启新的协程，在Dispatchers.Default中并行执行 - 并行切协程
             */
            launch(Dispatchers.Default) {
                println("2 - Default launch : ${thread()}")
            }

            /*
            挂起当前协程，在Dispatchers.IO执行完后返回当前协程继续执行 - 串行切协程
             */
            withContext(Dispatchers.IO){
                Thread.sleep(2000)
                println("3 - IO withContext : ${thread()}")
            }

            println("4 - Main launch : ${thread()}")
        }
    }
    /*
    withContext：挂起函数，切换协程的CoroutineContext
    指定执行的线程池，协程会等到withContext执行完成后继续执行（挂起函数本来就是挂起当前的协程）
    */
}