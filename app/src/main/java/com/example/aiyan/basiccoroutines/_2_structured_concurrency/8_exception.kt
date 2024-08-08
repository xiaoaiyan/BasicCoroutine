package com.example.aiyan.basiccoroutines._2_structured_concurrency

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 协程的异常
 *
 * 1、
 * try catch 协程的启动（launch，async）是无法捕获到协程的异常的
 * try catch 协程的业务代码（协程代码块中的代码）才能不会到协程的异常
 *
 * 2、
 * 协程的取消（cancel或者[CancellationException]）：取消自己和子协程，子协程的子协程（整个协程链，某个协程取消，向下取消）
 * 协程的异常：取消自己，子协程，还有父协程（整个协程链，某个协程异常，全会取消）
 *
 * 3、
 * CancellationException取消流程就是异常流程中的特例
 *
 * 4、
 * 普通的JVM应用中，异常只会使线程崩溃，应用崩溃是Android添加的逻辑
 *
 * 5、异常流程与取消流程的区别
 *      1、异常流程会取消自己、子协程，以及父协程
 *      子协程取消会调用父协程的childCancelled[JobSupport中]方法
 *      父子协程：流程上相互关系
 *
 *      2、异常流程只有抛异常（非CancellationException）才会触发
 *
 *      3、异常流程中的异常，不捕获，会抛到线程中去，导致线程崩溃
 */

fun main() {
    runBlocking {
        launch {
            launch {
                println("grand start")
                delay(3_000)
                println("grand end")
            }

            println("child start")
            delay(3_000)
            println("child end")
        }

        println("parent start")
        delay(1_000)
        cancel()
    }
}