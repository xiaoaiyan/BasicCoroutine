package com.example.aiyan.basiccoroutines._4_flow

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * flowOn：定制CoroutineContext，只会改变上游的CoroutineContext
 *
 * flowOn底层使用的和ChannelFlow同一套实现
 *
 * TODO 使用withContext切换CoroutineContext时，不能包含emit函数，否则会抛出异常（Flow不允许切换）
 *
 * flowOn和withContext：
 * 1、精细的切换，在单独的生产逻辑的代码块中某一部分想切换CoroutineContext
 * 2、上游全部切换，大范围的切换，使用flowOn
 *
 * flowOn的熔合（fuse）：当多个flowOn操作符连续调用时，会发生熔合（只会创建一个Flow对象，将coroutineContext参数相加）
 *
 * channelFlow{}.flowOn()：此时也会发生fuse
 */

fun main() = runBlocking<Unit> {
    //指定上游操作的CoroutineContext flowOn
    flow {
        println("coroutine context: ${currentCoroutineContext()}")
        emit(1)
    }.flowOn(Dispatchers.IO)
        .map {
            println("map coroutine context: ${currentCoroutineContext()}")
            it
        }.collect {
            println("collect coroutine context: ${currentCoroutineContext()}")
        }

    val flow = flowOf(1)
    //指定collect的CoroutineContext
    /*1*/
    launch(Dispatchers.Default) {
        flow.collect {
            println("collect coroutine context: ${currentCoroutineContext()}")
        }
    }
    /*2*/
    flow.onEach {
        println("onEach coroutine context: ${currentCoroutineContext()}")
    }.flowOn(Dispatchers.Default).collect()
    /*3*/
    flow.onEach {
        println("onEach coroutine context: ${currentCoroutineContext()}")
    }.launchIn(CoroutineScope(Dispatchers.Default))
}


