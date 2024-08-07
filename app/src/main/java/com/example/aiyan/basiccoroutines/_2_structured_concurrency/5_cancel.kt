package com.example.aiyan.basiccoroutines._2_structured_concurrency

import com.example.aiyan.basiccoroutines.cancelRetrofit
import com.example.aiyan.basiccoroutines.github
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.isActive
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

/**
 * 协程的取消（job.cancel()）
 *
 * 协程创建的线程：都是守护线程（用户线程结束，程序就结束了）
 *
 * 检查job的活跃状态（在coroutineScope中）
 * coroutineContext[Job]?.isActive
 * coroutineContext.job.isActive
 * coroutineContext.isActive
 * isActive
 * 都可以检查协程的活跃状态
 * 结束协程使用抛异常（CancellationException）
 *
 * 协程对于CancellationException会有特殊处理，结构化结束协程（协程和子协程）
 * return只能结束当前协程代码块，子协程不会结束（协程中不推荐使用return）
 *
 * 检查是否需要取消（在coroutineScope中），需要取消就抛CancellationException异常
 * ensureActive()
 * coroutineContext.ensureActive()
 * coroutineContext.job.ensureActive()
 * 如果结束协程，不需要额外的清理收尾，直接使用ensureActive就可以了
 *
 * 外部调用cancel时，如果协程处于等待，也会直接抛异常，CancellationException取消协程
 * 如果try catch捕获的是CancellationException，最好抛出来。否则，协程isActive为false，协程依然在运行
 *
 * 协程中，所有的挂起函数（除了suspendCoroutine挂起函数）在等待时，被取消，会抛出CancellationException异常，取消协程
 *
 */

fun main() = runBlocking<Unit> {
    val launch = launch(Dispatchers.Default) {
        var count = 0
        while (true) {
//            ensureActive()
//            suspendCoroutine<Int> {
//                it.resume(10)
//            }
//            suspendCancellableCoroutine<Int> {
//                println("suspend in suspendCancellableCoroutine")
//                //如果不调用resume函数，会一直挂起
//                it.resume(10)
//            }
            withContext(Dispatchers.IO){
                ""
            }
//            try {
//                github.contributors("square", "okhttp")
//            } catch (e: CancellationException) {
//                println("CancellationException: $e")
//                cancelRetrofit()
//                throw e
//            }
            println("count: ${count++}")
        }
    }
    delay(3_000)
    launch.cancel()
}