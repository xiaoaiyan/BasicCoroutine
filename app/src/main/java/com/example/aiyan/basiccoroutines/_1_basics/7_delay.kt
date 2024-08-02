package com.example.aiyan.basiccoroutines._1_basics

import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.concurrent.thread

/**
 * runBlocking 提供单线程的ContinuationInterceptor环境
 * launch时不指定ContinuationInterceptor的话，会全部运行在一个线程中
 *
 */
fun main() = runBlocking {
    //50_000个协程运行在1个线程
    repeat(50_000) {
        launch {
            delay(5_000)
            println(".")
        }
    }

    //运行在50_000个线程
    repeat(50_000) {
        thread {
            Thread.sleep(5_000)
            println(".")
        }
    }

    //延时 - 耗时
    //delay - sleep
    val singleThreadScheduledExecutor = Executors.newSingleThreadScheduledExecutor()
    repeat(50_000) {
        singleThreadScheduledExecutor.schedule({
            println(Thread.currentThread().name)
        }, 5, TimeUnit.SECONDS)
    }

    /**
     * delay：让出当前线程，线程干别的（挂起函数的优势）
     * sleep：线程停下来，啥也不干
     */
}