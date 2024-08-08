package com.example.aiyan.basiccoroutines._2_structured_concurrency

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.lang.Thread.UncaughtExceptionHandler
import kotlin.concurrent.thread
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 结构化异常（未知异常）
 *
 * 1、线程中的[UncaughtExceptionHandler] - 处理未捕获的异常
 *      Thread.setDefaultUncaughtExceptionHandler - 全局设置
 *      thread.setUncaughtExceptionHandler - 某一个线程设置
 *
 * 2、知道哪里会有异常，try catch捕获，处理掉就行了，程序会正常运行
 * [UncaughtExceptionHandler] 捕获到的是未知异常，线程已经结束运行了，仅仅能做些收尾（记录异常）
 *
 * 3、协程中的[CoroutineExceptionHandler]：设置在某个协程上（最外层的协程），针对某个协程树善后，捕获未知异常
 * 异常会先经过协程的 CoroutineExceptionHandler，然后在经过线程中的 UncaughtExceptionHandler
 *
 * 设置CoroutineExceptionHandler本质上是针对未知异常的善后方案（协程的异常流程是针对未知异常的善后方案）
 *
 * force close：强制退出
 */

fun main() {
    val uncaughtExceptionHandler = UncaughtExceptionHandler { _, exception ->
        println("uncaughtExceptionHandler $exception")
    }
    Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler)
//    thread {
//        throw NullPointerException("thread null pointer")
//    }

    val coroutineExceptionHandler = CoroutineExceptionHandler{
        coroutineContext, exception ->
        println("coroutineExceptionHandler $exception, coroutineContext: ${coroutineContext[Job]}")
    }

    CoroutineScope(EmptyCoroutineContext + coroutineExceptionHandler).launch {
        throw NullPointerException("coroutine null pointer")
    }

    Thread.sleep(3_000)
}