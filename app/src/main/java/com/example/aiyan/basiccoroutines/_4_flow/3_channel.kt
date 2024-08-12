package com.example.aiyan.basiccoroutines._4_flow

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * channel：实现SendChannel 和 ReceiveChannel（单向通道、管道）
 *
 * Channel 同时实现了SendChannel和ReceiveChannel，创建的Channel对象
 * 既能send发送数据，也能receive接收数据
 *
 * produce启动的协程ProducerScope
 * 创建的SendChannel和ReceiveChannel都是同一个对象ProducerCoroutine
 *
 * produce函数就只是将channel的创建和协程结合在了一起，返回receiveChannel，可以在其他协程通过receive接受数据
 *
 * Channel分为ReceiveChannel和SendChannel，只是为了API暴露，尽量返回干净的API
 *
 * channel：本质上就是协程版的阻塞队列，只是将阻塞线程替换为挂起协程
 *
 * channel：不适合做事件订阅，超过1个订阅者，就会瓜分事件
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun main() = runBlocking<Unit> {
    val receiveChannel: ReceiveChannel<Int> = produce<Int> {
        println("produceScope: $this")
    }
    println("receiveChannel $receiveChannel")

    /**
     * Channel<Int>()：工厂函数，根据参数创建不同的channel对象
     * 就可以在不同的协程之中，使用send发送数据，receive接受数据（实现了跨协程通信）
     */
    val channel = Channel<Int>()
    launch {
        channel.send(10)
    }
    launch {
        channel.send(100)
    }
    launch {
        println(channel.receive())
        println(channel.receive())
    }
}