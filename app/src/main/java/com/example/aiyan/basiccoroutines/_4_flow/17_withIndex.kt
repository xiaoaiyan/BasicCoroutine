package com.example.aiyan.basiccoroutines._4_flow

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.withIndex
import kotlinx.coroutines.runBlocking

/**
 * withIndex：给元素添加序号
 */

fun main() = runBlocking<Unit> {
    flowOf(1, 2, 3, 4, 5).withIndex().collect{
        println("withIndex ${it.index} ${it.value}")
    }
}