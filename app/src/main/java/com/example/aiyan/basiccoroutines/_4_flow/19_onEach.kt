package com.example.aiyan.basiccoroutines._4_flow

import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

/**
 * onEach：数据监听（可以多次调用，每次都是对调用它的flow对象进行数据监听）
 */

fun main() = runBlocking<Unit> {
    //onEach：监听每一轮上游发送的数据
    flowOf(1, 2, 3, 4, 5)
        .onEach {
            println("flowOf onEach $it")
        }.map { it * 2 }
        .onEach {
            println("map onEach $it")
        }.collect()
}