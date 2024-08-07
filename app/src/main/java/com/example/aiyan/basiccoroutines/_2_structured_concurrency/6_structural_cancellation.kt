package com.example.aiyan.basiccoroutines._2_structured_concurrency

import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * 结构化取消
 *
 * 父协程取消会连带着将其子协程取消
 *
 * 协程取消的本质（交互式取消）：
 * 1、job.cancel方法将协程的取消状态设置为true
 * 2、协程块代码需要有配合，检查job.isActive状态，抛出CancellationException异常结束协程
 *  或者内部调用可挂起函数
 *
 *  父协程的cancel会触发所有子协程的cancel
 *
 *  父协程cancel发生了什么？？？
 *  1、改变自己的isActive为false
 *  2、调用子协程job的cancel方法，递归调用，将所有的子协程的isActive为false
 *  3、所有协程需要有配合，检查到isActive状态改变后抛出CancellationException异常结束协程
 *      （所有协程什么时候抛出？？？协程代码运行到了检查点，如果没有检查点，协程甚至不会结束）
 *  4、协程抛出CancellationException异常后，还会改变自己以及子协程的isActive为false
 *      为什么重复？？？协程可以通过抛出CancellationException异常结束自己和子协程
 *
 *  子协程不支持结束，会导致父协程也无法结束
 */
fun main() = runBlocking<Unit> {
    val parent = CoroutineScope(EmptyCoroutineContext).launch {
        val child = launch {
            println("child coroutine start")
            suspendCancellableCoroutine {
                Thread.sleep(3_000)
                it.resume("suspendCancellableCoroutine")
            }
            println("child coroutine end")
        }
        delay(1_000)
        //取消父协程cancel方法或者抛出CancellationException异常
        throw CancellationException()
    }
    delay(10_000)
}