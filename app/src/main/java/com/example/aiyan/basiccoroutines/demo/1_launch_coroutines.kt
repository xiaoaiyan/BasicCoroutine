package com.example.aiyan.basiccoroutines.demo

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.EmptyCoroutineContext

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