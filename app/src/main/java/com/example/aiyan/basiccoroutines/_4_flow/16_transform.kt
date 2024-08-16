package com.example.aiyan.basiccoroutines._4_flow

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.flow.transformWhile
import kotlinx.coroutines.runBlocking

/**
 * transform：转换数据后发送到下游（需要手动调用emit发送数据）
 *  ----底层map，更加自由
 */

@OptIn(ExperimentalCoroutinesApi::class)
fun main() = runBlocking<Unit> {
    flowOf(1, 2, 3, 4, 5).transform {
        if (it and 1 == 0)
            emit(it * it)
    }.collect {
        println("transform: $it")
    }

    //transformWhile：相比于transform有个boolean返回值，如果返回false，则停止转换
    flowOf(1, 2, 3, 4, 5).transformWhile {
        if (it > 3) return@transformWhile false
        if (it and 1 == 0)
            emit(it * it)
        true
    }.collect {
        println("transformWhile: $it")
    }

    //transformLatest：类似mapLatest，有新的数据进来就舍弃旧的转换，转换新的数据
    flow {
        emit(1)
        delay(500)
        emit(2)
        delay(500)
        emit(3)
    }.transformLatest {
        delay(100)
        emit("$it start")
        delay(500)
        emit("$it end")
    }.collect {
        println("transformLatest: $it")
    }
}