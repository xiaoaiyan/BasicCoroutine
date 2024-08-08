package com.example.aiyan.basiccoroutines._2_structured_concurrency

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

/**
 * SupervisorJob：子协程因为异常取消的，父协程不会被取消（childCancelled函数中）
 *
 * SupervisorJob，取消，子协程会连带着取消
 * 子协程因为异常取消时，SupervisorJob不会被连带着取消
 *
 * 1、CoroutineScope(SupervisorJob()) - 当前scope启动的多个协程链，其中一个因为异常取消，不会影响到其他的协程链取消
 * 2、SupervisorJob(coroutineContext.job) - 链接父子协程，父协程取消，子协程取消，子协程异常取消，不会影响到父协程
 *      launch(SupervisorJob(coroutineContext.job) + handler){} - 此时就算不是最外层的协程，handler也能不会到子协程中的异常
 */

fun main() {
    val coroutineScope = CoroutineScope(SupervisorJob())
//    val coroutineScope = CoroutineScope(EmptyCoroutineContext)
    coroutineScope.launch {
        println("launch in")
        delay(3_000)
        println("launch out")
    }
    coroutineScope.launch {
        delay(1_000)
        throw RuntimeException("RuntimeException")
    }
    coroutineScope.launch(CoroutineExceptionHandler{coroutineContext, throwable ->
        println("out throwable = $throwable")
    }) {
        val handler = CoroutineExceptionHandler{coroutineContext, throwable ->
            println("throwable = $throwable")
        }
        launch(SupervisorJob(coroutineContext.job) + handler) {
            launch {

                launch {
                    delay(1_000)
                    throw RuntimeException("RuntimeException")
                }
                delay(3_000)
                println("not finish")
            }
        }
    }
    Thread.sleep(5000)
}