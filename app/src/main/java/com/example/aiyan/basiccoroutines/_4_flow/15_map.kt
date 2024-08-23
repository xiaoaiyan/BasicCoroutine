package com.example.aiyan.basiccoroutines._4_flow

/**
 * map：将上游数据经过map代码块转换为新的数据
 */

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.flow.mapNotNull
import kotlinx.coroutines.runBlocking

@OptIn(ExperimentalCoroutinesApi::class)
fun main() = runBlocking<Unit> {

    //map
    flowOf(1, 2, 3, 4, 5).map {
        it * 2
    }.collect {
        println("map: $it")
    }

    //mapNotNull：发送转换结果不是null的元素
    flowOf(1, 2, 3, 4, 5).mapNotNull {
        if (it == 1) null else it * it
    }.collect {
        println("mapNotNull: $it")
    }

    //mapLatest：相比于其他的操作符，mapLatest异步接收上游的数据，当接收到新的数据时
    //如果旧的数据依然还在处理，舍弃旧的数据，处理新的数据
    //底层通过使用channelFlow实现
    // TODO: mapLatest：数据到来时如果正在转换数据则取消旧的转换，开始新的转换
    flow {
        emit(1)
        delay(500)
        emit(2)
        delay(500)
        emit(3)
    }.mapLatest {
        delay(1000)
        it * 2
    }.collect {
        println("mapLatest: $it")
    }
    //结果：
    //mapLatest: 6
}