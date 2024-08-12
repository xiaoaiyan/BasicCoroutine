package com.example.aiyan.basiccoroutines._4_flow

import com.example.aiyan.basiccoroutines.Contributor
import com.example.aiyan.basiccoroutines.cancelRetrofit
import com.example.aiyan.basiccoroutines.github
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.ReceiveChannel
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * produce:
 *
 * 异步操作的启动和结果的获取分开，分别在不同的协程中进行。
 *
 * SSE：server send event
 *
 * produce：启动协程（启动的是生产数据的协程）,就可以当成使用launch启动的协程一样
 *
 * channel应用：（从一个协程向另一个协程提供数据流）
 * 1、通过CoroutineScope().produce{}函数创建一个内部提供SendChannel的协程，获得方法返回的ReceiveChannel
 * 2、协程内部通过send挂起函数发送数据
 * 3、开启新协程，通过receiveChannel.receive挂起函数获取数据
 *
 * public interface ProducerScope<in E> : CoroutineScope, SendChannel<E> {
 *     public val channel: SendChannel<E>
 * }
 */

@OptIn(ExperimentalCoroutinesApi::class, DelicateCoroutinesApi::class)
fun main() = runBlocking<Unit> {
    val parent = GlobalScope.launch(Dispatchers.IO) {
        val receiveChannel: ReceiveChannel<List<Contributor>> = produce {
            while (isActive) {
                try {
                    coroutineScope {
                        val contributors = github.contributors("square", "retrofit")
                        send(contributors)
                        delay(5000)
                    }
                } catch (e: Exception) {
                    cancelRetrofit()
                    throw e
                }
            }
        }

        launch {
            while (isActive) {
                val contributors = receiveChannel.receive()
                println(contributors)
            }
        }
    }
    delay(10_000)
    parent.cancel()
}