package com.example.aiyan.basiccoroutines.reflections

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 协程切线程：（启动协程的方式、挂起函数的方式）
 *
 * 1、coroutineScope.launch在指定的线程启动协程实现切线程
 * ————业务需要并行时，直接使用coroutineScope启动多个并行的协程
 * 2、在协程内部调用系统提供的切换线程的挂起函数（如：withContext）
 * ————业务需要串行时，启动一个协程，在协程中使用挂起函数切换线程
 */

@OptIn(ExperimentalCoroutinesApi::class)
fun main() {
    /**
     * StandaloneCoroutine == AbstractCoroutine == （Job，CoroutineScope）
     */
    val scope = CoroutineScope(EmptyCoroutineContext)
    val parent = scope.launch {
        val parentJob: Job = coroutineContext[Job]!!

        val launchChild = launch {
            info("launch")
            delay(100)
        }

        val asyncChild = async {
            info("async")
            delay(100)
        }

        withContext(parentJob) {
            info("withContext")
            delay(100)
        }

        coroutineScope {
            info("coroutineScope")
            delay(100)
        }
    }

    Thread.sleep(50)
    println(parent.children.count())
    Thread.sleep(500)
}

@OptIn(ExperimentalCoroutinesApi::class)
private fun CoroutineScope.info(additionalInfo: String) {
    println(
        """
        $additionalInfo
        thread = ${Thread.currentThread().name}
        job = ${coroutineContext[Job]}
        parentJob = ${coroutineContext[Job]!!.parent}
        ======================================
    """.trimIndent()
    )
}