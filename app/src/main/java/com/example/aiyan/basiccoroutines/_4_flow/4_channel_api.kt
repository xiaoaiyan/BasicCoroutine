package com.example.aiyan.basiccoroutines._4_flow

import com.example.aiyan.basiccoroutines.Contributor
import com.example.aiyan.basiccoroutines.github
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * send默认效果：当队列满时，挂起协程（队列长度默认为0），所以调用send就会挂起等着receive
 *
 * TODO Channel()的参数
 * 1、capacity：缓冲区大小，默认为0（RENDEZVOUS）
 *      缓冲区满后，再次send就会挂起协程，等着receive接收数据，等缓冲区有空余未知
 * UNLIMITED：缓冲大小无限制，能一直send发送数据，直到Channel关闭
 * RENDEZVOUS == 0：无缓冲区
 * BUFFERED：使用系统默认的配置值，64
 * CONFLATED（结合）：相当于缓冲区为1和缓冲策略为DROP_OLDEST（注意：创建时capacity=CONFLATED，onBufferOverflow必须为SUSPEND）
 *      始终保持最新的数据
 * 具体值：某个具体的int值
 *
 * 2、onBufferOverflow：缓冲区溢出策略（默认SUSPEND，挂起）
 * SUSPEND：挂起
 * DROP_OLDEST：丢弃最早的数据
 * DROP_LATEST：丢弃最新数据（send进来的新元素）
 *
 * 3、onUndeliveredElement
 *
 * TODO channel的关闭：
 * 1、SendChannel：close()关闭发送功能
 *     [isCloseForSend] - 是否关闭发送功能，默认为false（为True后，send就会抛出ClosedSendChannelException异常）
 *     channel调用close关闭后，任何协程调用send都会抛出ClosedSendChannelException异常（由开发者从整体管理是否该关闭发送功能）
 *     而在close之前，对于已经处于send挂起的协程是不会抛异常，正常发送
 *
 *     ReceiveChannel的[isClosedForReceive] - 是否关闭接受功能
 *     SendChannel close关闭后，如果缓冲区有数据，或者有处于send挂起的协程，ReceiveChannel依然可以调用receive获取数据，不会抛出异常
 *     此时对于ReceiveChannel的isClosedForReceive为false，当所有的数据接受完毕后，isClosedForReceive为true，再次receive就会抛出ClosedReceiveChannelException异常
 *
 * 2、ReceiveChannel：cancel() - 不再接受新的数据，取消接收功能
 *     [isClosedForReceive] 和 [isClosedForSend] 全部为true，send和receive都会抛出异常（CancellationException）
 *
 *     cancel之后所有的数据（缓冲区数据、send挂起数据都会被丢弃，交给onUndeliveredElement处理）不再接收，
 *     后续的如果发送数据也会全部扔给onUndeliveredElement处理
 *
 * trySend 和 tryReceive 尝试发送和接收，非挂起函数，瞬时返回结果[ChannelResult]
 *
 * receiveCatching：接收数据（异常也会接收），返回ChannelResult，自己处理异常
 */

@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
fun main() = runBlocking<Unit> {
    //capacity == 0，bufferOverflow需要为SUSPEND，否则capacity会被修改为1
    val channel = Channel<Int>(1, BufferOverflow.DROP_OLDEST)
    launch {
        channel.send(1)
        channel.send(2)
        channel.send(3)
    }
    launch {
        for (value in channel){
            println("value: $value")
        }
    }

    /**
     * SendChannel.close ----------------------------------------------------------
     */
    val stringChannel = Channel<String>(4) {
        println("onUndeliveredElement: $it")
    }
    launch(Dispatchers.Default) {
        var value = "1"
        while (isActive){
            val data = requestData(value)
            try {
                stringChannel.send(data)
            } catch (e: Exception) {
                println("send exception: $e, is closed for send: ${stringChannel.isClosedForSend}")
//                throw e
            }
            value = value.toInt().plus(1).toString()
        }
    }
    launch(Dispatchers.Default) {
        delay(5_000)
        for (data in stringChannel){
            println("receive: $data")
            if (data == "2") stringChannel.close()
        }
        try {
            stringChannel.receive()
        } catch (e: Exception) {
            println("receive exception: $e, is closed for receive: ${stringChannel.isClosedForReceive}")
//            throw e
        }
    }

    /**
     * ReceiveChannel.cancel ----------------------------------------------------------
     */
//    val intChannel = Channel<Int>(4) {
//        println("onUndeliveredElement: $it")
//    }
//    launch(Dispatchers.Default) {
//        var value = 1
//        while (isActive) {
//            val requestData = requestData(value)
//            try {
//                intChannel.send(requestData)
//            } catch (e: Exception) {
//                println("send exception: $e, is closed for send: ${intChannel.isClosedForSend}")
//                /**
//                 * 异常抛出才会改变isActive，协程结束，否则一直send抛异常
//                 */
//                throw e
//            }
//            value++
//        }
//    }
//
//    /*
//    调用receiveChannel的cancel后，isClosedForSend 和 isClosedForReceive全部变为true，send和receive都会抛出异常（CancellationException）
//    然后进入异常流程，isActive变为false，协程结束
//    */
//    launch {
//        delay(5_000)
//        val receive = intChannel.receive()
//        println("receive: $receive")
//        intChannel.cancel()
//        try {
//            intChannel.receive()
//        } catch (e: Exception) {
//            println("receive exception: $e, is closed for receive: ${intChannel.isClosedForReceive}")
//        }
//    }


    /**
     * CoroutineScope.produce ----------------------------------------------------------
     */
//    val receiveChannel = produce<List<Contributor>> {
//        val contributors = github.contributors("JetBrains", "Kotlin")
//        send(contributors)
//        send(contributors)
//        send(contributors)
//    }
//
//    launch {
//        /**
//         * 循环遍历：当下一个元素出现之前，挂起协程（或者channel关闭）
//         *
//         * 在close之后，接受完数据，就会结束循环，继续执行下一行代码
//         * 那如果cancel之后那？？？
//         */
//        for (contributors in receiveChannel) {
//            println(contributors)
//        }
//
//        /**
//         * 类似上面的实现
//         */
//        while (isActive){
//            val contributors = intChannel.receive()
//            println(contributors)
//        }
//    }
}

private suspend fun <T> requestData(data: T) = coroutineScope {
    delay(1_000)
    data
}