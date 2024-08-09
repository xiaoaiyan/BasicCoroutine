package com.example.aiyan.basiccoroutines._3_scope_context

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.coroutineContext

/**
 * 从挂起函数得到CoroutineContext
 *
 * 1、调用挂起函数：coroutineContext（字段的get方法是挂起函数）
 * 2、调用挂起函数：currentCoroutineContext()
 */

fun main() {
    CoroutineScope(EmptyCoroutineContext).launch {
        suspendFunctionCoroutineContext()
    }
    Thread.sleep(1000)
}

private suspend fun suspendFunctionCoroutineContext(){
    println("coroutineContext = $coroutineContext")
    println("currentCoroutineContext() = ${currentCoroutineContext()}")
}


/**
 * 挂起函数中的：coroutineContext 和 currentCoroutineContext()
 *
 * 解决命名冲突问题
 *
 * 当处于挂起函数和协程双重环境中时，正确获取到挂起函数中的CoroutineContext使用，currentCoroutineContext
 */
@OptIn(DelicateCoroutinesApi::class)
private fun flowFun(){
    flow<Int> {
        coroutineContext
    }

    GlobalScope.launch {
        flow<Int> {
            coroutineContext
            currentCoroutineContext()
        }

        channelFlow<Int> {
            coroutineContext
            currentCoroutineContext()
        }
    }
}