package com.example.aiyan.basiccoroutines._3_scope_context

import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * 协程名CoroutineName - 用来测试和调试
 *
 * 具备协程上下文该有的都有
 */

fun main() {
    runBlocking {
        val coroutineName = CoroutineName("协程呀")
        CoroutineScope(Dispatchers.Default + coroutineName)
        launch(coroutineName){
            println(coroutineContext[CoroutineName]!!.name)

        }
    }
}