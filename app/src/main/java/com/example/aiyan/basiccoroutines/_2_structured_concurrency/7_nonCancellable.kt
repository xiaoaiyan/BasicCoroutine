package com.example.aiyan.basiccoroutines._2_structured_concurrency

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.cancel
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 什么时候希望协程不需要取消？？？[NonCancellable] - 单例Job对象，与启动协程的CoroutineScope断开父子关系
 *
 * 1、协程被cancel结束之前的收尾工作
 *  如果收尾工作包含挂起函数：withContext(NonCancellable){}，避免CancellationException
 *
 * 2、如果取消后，很难处理的业务代码（有些耗时操作，要么别开始，要么就做完）
 *  withContext(NonCancellable + Dispatchers.IO){}
 *
 * 3、当前业务与协程流程无关（日志任务）
 *  launch(NonCancellable){
 *  }
 */
fun main() = runBlocking<Unit> {
    val parent = CoroutineScope(EmptyCoroutineContext).launch {
        launch {
            println("launch coroutine start")
            while (true){
                if (!isActive){
                    //清理工作
                    try {
                        withContext(Dispatchers.IO + NonCancellable){
                            println("clean finish")
                        }
                    } catch (e: Exception) {
                        println("clean exception: $e")
                        throw e
                    }
                    //上面的挂起函数就会抛出异常，所以下面的代码不会执行
                    println("===================")
                    throw CancellationException()
                }
                Thread.sleep(500)
            }
        }

        //cancellable协程
        launch(NonCancellable) {
            println("NonCancellable launch coroutine start")
            delay(3_000)
            println("NonCancellable launch coroutine end")
        }
    }
    delay(1_000)
    parent.cancel()

    delay(10_000)
}