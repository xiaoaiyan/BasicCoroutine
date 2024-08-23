package com.example.aiyan.basiccoroutines._4_flow

/**
 * buffer：缓冲 ---- 给Flow加上缓冲功能
 *
 * flow是线性的：
 * 1、每条数据都是沿着flow链从上到下到达collect
 * 2、整个数据流，多条数据是上一条数据到达collect后执行完，才开始执行下一条数据
 *
 * 如何并行呢？？？
 * ----使用flowOn将生产和处理放在不同的协程之中
 *
 * 底层使用ChannelFlow的操作符：flowOn，buffer
 *
 * flowOn：指定生产数据的协程环境，但是也会开启缓冲（底层channel的capacity=BUFFERED，overflow=SUSPEND）
 * buffer：使用EmptyCoroutineContext开启新的协程，channel的capacity=BUFFERED，overflow=SUSPEND
 *
 * ChannelFlow的熔合：
 * 1、CoroutineContext：
 *  后面的 + 前面的，-> 有相同的CoroutineContext时前面优先
 * 2、Channel的capacity和onBufferOverflow
 * 1、缓存溢出策略不是SUSPEND，直接使用新的capacity和onBufferOverflow
 * 2、缓存溢出策略是SUSPEND，不变，都具体设置了capacity，相加，否则BUFFERED
 */

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.InternalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.buffer
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.internal.SendingCollector
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

@OptIn(ExperimentalCoroutinesApi::class)
fun main() = runBlocking<Unit> {
    /**
     * collectLatest：数据处理过程中有新的数据来时，取消旧数据的处理，开始处理新数据
     */
    emitData(20).collectLatest {
        delay(1_000)
        println("value: $it, coroutineContext: ${currentCoroutineContext()}")
    }

    emitData(5).mapLatest {
        delay(1_000)
        println("value: $it, coroutineContext: ${currentCoroutineContext()}")
    }.collect()

    /**
     * mapLatest：收到新数据时，取消旧数据的转换，继而开始新数据的转换
     * 转换完的数据会放入缓冲之中等待下游获取
     *
     * mapLatest底层源码：收集到上游数据后，cancelAndJoin，保证结束之前的转换，开启协程运行转换逻辑
     */
    emitData(5).mapLatest { it }.collect{
        delay(1_000)
        println("value: $it, coroutineContext: ${currentCoroutineContext()}")
    }

    emitData(10).toCustomChannelFlow().collect{
        delay(1_000)
        println("value: $it, coroutineContext: ${currentCoroutineContext()}")
    }
}

private fun emitData(num: Int) = flow<Int> {
    for (data in 1..num) {
        emit(data)
        println("emit data $data coroutineContext: ${currentCoroutineContext()}")
        delay(data * 100L)
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
private suspend fun <T> Flow<T>.myCollectLatest(action: suspend (value: T) -> Unit) {
    val mapLatest = mapLatest(action)
    val buffer = mapLatest.buffer(0)
    buffer.collect()
}

/**
 * channelFlow核心逻辑模拟
 */
private class CustomChannelFlow<T>(
    private val capacity: Int = Channel.BUFFERED,
    private val block: suspend FlowCollector<T>.() -> Unit) : Flow<T> {
    @OptIn(ExperimentalCoroutinesApi::class, InternalCoroutinesApi::class)
    override suspend fun collect(collector: FlowCollector<T>) {
        coroutineScope {
            val receiveChannel = produce<T>(capacity = capacity) {
                block(SendingCollector(this))
            }
            for (value in receiveChannel) {
                collector.emit(value)
            }
        }
    }
}

private fun <T> Flow<T>.toCustomChannelFlow(capacity: Int = Channel.BUFFERED): Flow<T> = CustomChannelFlow(capacity){
    collect(this)
}