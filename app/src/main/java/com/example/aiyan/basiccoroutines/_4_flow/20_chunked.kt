package com.example.aiyan.basiccoroutines._4_flow

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.chunked
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking

/**
 * chunk：将数据按照指定大小分块后，传递到下游
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun main() = runBlocking<Unit> {
    flowOf(1, 2, 3, 4, 5).chunked(2).collect{
        println("chunked: $it")
    }
}