package com.example.aiyan.basiccoroutines._4_flow

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectIndexed
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

/**
 * 收集collect
 *
 * flow不允许切换协程发送数据？？？ - 方便开发者
 * ----为了使处理数据的操作就在collect所在的协程，符合开发者的直觉，而不需要开发者每次在处理数据时都要考虑切换上下文
 *
 * collect代码块在哪里执行？？？ - emit在哪里发送，collect代码块就在哪里执行
 *
 * collect就是
 *  flow.collect(object : FlowCollector<Int>{
 *      override suspend fun emit(value: Int) {
 *
 *      }
 *  })
 *
 *  处理数据应该在collect所在的协程，而不是应该在emit发送数据的协程，如何实现？？？
 *  1、处理数据时自己切换协程
 *  2、禁止从其他协程发送数据，只能在collect所在的协程发送数据 ---- 协程方案
 *
 *  launchIn：提供CoroutineScope，指定启动协程的作用域
 */

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    val flow: Flow<Int> = flow {
        var value = 1
        while (currentCoroutineContext().isActive) {
            emit(value++)
            delay(1000)
        }
    }

//    flow.onEach {
//        handleData(it)
//    }.launchIn(GlobalScope)

    val launch = GlobalScope.launch {
        flow.collect {
            handleData(it)
        }
        flow.collectIndexed { index, value ->
            handleData(value)
        }
        flow.collectLatest {

        }
    }

    runBlocking {
        launch.join()
    }
}

@OptIn(DelicateCoroutinesApi::class)
private fun errorUse() {
    /**
     * collect 之后 等价于下面的代码执行效果
     * withContext(Dispatchers.IO) {
     *      handleData(10)
     * }
     * delay(1000)
     * handleData(20)
     */
    //错误，flow不允许切换协程发送数据
    val flow = flow {
        withContext(Dispatchers.IO) {
            emit(10)
        }
        emit(20)
    }
    GlobalScope.launch {
        flow.collect {
            handleData(it)
        }
    }
}

private fun handleData(it: Int) {
    println(it)
}
