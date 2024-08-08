package com.example.aiyan.basiccoroutines._2_structured_concurrency

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlin.concurrent.thread
import kotlin.coroutines.EmptyCoroutineContext

/**
 * try {
 *      CoroutineScope(EmptyCoroutineContext).launch {
 *      }
 * }catch (exception: Exception){
 *     exception.printStackTrace()
 * }
 * 上面的代码只能捕获到协程启动的异常，协程执行的异常，无法监听到
 *
 * 相当于
 * try {
 *      thread {}
 * } catch (e: Exception) {
 *    e.printStackTrace()
 * }
 * 只是在捕获线程启动的异常，也无法捕获线程执行过程中的异常
 */
fun main(){
    val coroutineExceptionHandler = CoroutineExceptionHandler{ _, throwable ->
        println("Exception handled: $throwable")
    }

    /**
     * 在最外层的启动协程中添加[CoroutineExceptionHandler]，就能将整个协程树中未捕获的异常全部抓到，不会抛到线程世界中去
     *
     * 只能设置到最外层的父协程之中
     */
    CoroutineScope(EmptyCoroutineContext).launch(coroutineExceptionHandler) {
        throw NullPointerException()
    }

    Thread.sleep(10_000)
}