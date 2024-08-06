package com.example.aiyan.basiccoroutines._2_structured_concurrency

import kotlin.concurrent.thread

/**
 * thread.stop() - 直接野蛮结束线程，会造成不可预知的问题（早就废弃了，不推荐使用）
 */

fun main() {
    val thread = thread {
        println("Thread started")
        Thread.sleep(3_000)
        println("Thread ended")
    }
    Thread.sleep(1_000)
    thread.stop()
}