package com.example.aiyan.basiccoroutines._1_basics

import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import com.example.aiyan.basiccoroutines.theme.BasicCoroutinesTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.newFixedThreadPoolContext
import java.util.concurrent.Executors
import kotlin.coroutines.EmptyCoroutineContext

class LaunchCoroutinesActivity : ComponentActivity() {
    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BasicCoroutinesTheme {
                Text(text = "Hello")
            }
        }

        /**
         * 何时需要切线程：
         * 1、切换线程执行，不挡住当前线程的执行流程，开启并行线程
         * 2、Android应用，切换到UI线程，Handler
         */

        //线程API，使用线程池
        val executors = Executors.newCachedThreadPool()
        executors.execute {
            println("executors thread: ${Thread.currentThread().name}")
        }
        View(this).post {

        }

        println("main thread: ${Thread.currentThread().name}")

        //协程API
        val coroutineScope = CoroutineScope(EmptyCoroutineContext)
        //函数类型对象，交给线程池/UI线程执行
        coroutineScope.launch {
            println("coroutine thread: ${Thread.currentThread().name}")
        }

        //ContinuationInterceptor：继续拦截器（拦截一下，做点别的-切线程，继续执行），用来指定协程的执行线程
        //唯一实现子类：CoroutineDispatcher协程调度器

        //系统提供的CoroutineDispatcher

        /*
        启动协程默认运行的线程池，计算密集型（线程池大小==CPU核心数 ）Runtime.getRuntime().availableProcessors()
         */
        Dispatchers.Default
        /*
        UI线程
         */
        Dispatchers.Main
        /*
        用不到，不了解
         */
        Dispatchers.Unconfined //启动协程不切线程，挂起函数切线程执行完后不会切回去
        /*
        IO密集型，网络读写，磁盘读写等（64个线程的线程池）
         */
        Dispatchers.IO

        /**
         * 指定自己创建的线程池
         */
        val newFixedThreadPoolContext = newFixedThreadPoolContext(10, "thread")
        CoroutineScope(newFixedThreadPoolContext).launch {

        }
        /*
        不再使用是，需要自己手动关闭线程池，防止泄漏
         */
        newFixedThreadPoolContext.close()

        /**
         * 协程线程池中的线程都是守护线程
         */
    }
}