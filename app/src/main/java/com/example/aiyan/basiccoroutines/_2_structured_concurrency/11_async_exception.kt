package com.example.aiyan.basiccoroutines._2_structured_concurrency

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 启动协程：1、launch 2、async
 *
 * async的异常管理：
 * async代码块中出现异常，会影响启动async所在的协程树和await所在的协程树
 *
 * 协程取消（isActive=false），运行所有的挂起函数都会抛出CancellationException异常
 *
 * 1、async不会将异常抛到线程世界，而是在await的地方抛出，因为它不是终点（从launch出来下一站是线程世界，从async出来，下一站是await的协程世界）
 * 所以async作为在外层协程时，无法通过CoroutineExceptionHandler捕获异常
 * 由async作为在外层协程，协程树中的协程出现异常时，协程会进入异常取消流程，但是在该协程树中无法捕获到异常，只能在await调用所在的协程树中才能捕获到异常
 *
 * 2、async协程异常（RuntimeException）
 *  await在同一协程树，await会受双重影响，RuntimeException和CancellationException，
 *  不过RuntimeException会比结构化的取消异常CancellationException优先到达
 *  await不在同一协程树，await所在协程会收到RuntimeException影响，进入异常取消流程
 */

fun main() {
    val coroutineExceptionHandler = CoroutineExceptionHandler{context, exception ->
        println("coroutineExceptionHandler: $exception")
    }

//    val deferred = CoroutineScope(EmptyCoroutineContext + coroutineExceptionHandler).async {
//
//        launch {
//            delay(1_000)
//            throw RuntimeException("RuntimeException")
//        }
//
//        try {
//            delay(3_000)
//        } catch (exception: Exception) {
//            println(exception)
//        }
//    }
//    CoroutineScope(EmptyCoroutineContext).launch {
//        deferred.await()
//    }

    CoroutineScope(EmptyCoroutineContext + coroutineExceptionHandler).launch {
        val deferred = async {
            delay(1_000)
            throw RuntimeException("RuntimeException")
        }

        try {
            deferred.await()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            delay(1000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
    Thread.sleep(5_000)
}
