package com.example.aiyan.basiccoroutines._2_structured_concurrency

import kotlin.concurrent.thread


/**
 * interrupt结束线程（交互式结束线程，需要线程内部支持中断机制）
 *
 * 标记线程中断状态，告诉线程你该结束了，是否结束，还是在于线程自己是否处理了中断
 *
 * 从线程外部调用thread.interrupt()才有意义（线程内部想结束，直接return就可以）
 *
 * 线程如果被标记为中断状态，睡眠、等待等都没必要，会直接抛出InterruptedException
 * （等待性质的方法thread.sleep，object.wait，thread.join，countDownLatch.await等）
 * 抛出InterruptedException后，中断标识会被清除
 *
 * 检查中断标识的方法：
 * 1、Thread.interrupted() - 返回中断标识，并清除中断标识，第二次调用返回false
 * 2、Thread.currentThread().isInterrupted - 返回中断标识，不清除中断标识，第二次调用返回true
 *
 * 实践中：在关键耗时节点主动检查中断状态标识，在等待方法包裹try catch捕获InterruptedException，清理，return结束线程
 */
fun main() {
    val thread = thread {
        var value = 1
        while (true) {
            if (Thread.currentThread().isInterrupted) {
                return@thread
            }
            if (value % 100_000_000 == 0) {
                println("value = $value")
                try {
                    Thread.sleep(500)
                } catch (e: InterruptedException) {
                    println("sleep interrupted")
                    return@thread
                }
            }
            if (value == 1_000_000_000) {
                return@thread
            }
            value++
        }
    }

    Thread.sleep(3_000)
    thread.interrupt()
}