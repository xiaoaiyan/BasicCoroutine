package com.example.aiyan.basiccoroutines._4_flow

import com.example.aiyan.basiccoroutines.Contributor
import com.example.aiyan.basiccoroutines.github
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * flow的创建
 *
 * flow 的 emit 是不允许跨协程的，flow代码块在哪里运行，emit就必须在同样的协程emit
 */
fun main() {
    val flow1 = flow {
        emit(1)
        emit(2)
        emit(3)
    }

    //
    val flow2 = flowOf(1, 2, 3)

    //
    val flow3 = listOf(1, 2, 3).asFlow()

    //
    setOf(1, 2, 3).asFlow()

    //
    sequenceOf(1, 2, 3).asFlow()

    //冷的还是热的？？？
    val channel = Channel<Int>()
    channel.consumeAsFlow() //只能被调用一次collect
    channel.receiveAsFlow() //可以多次调用collect，但是会瓜分数据

    //每次collect就会创建channel来生成数据，多次调用会有多个channel
    //channelFlow：主要是可以启动协程从而在不同的协程中生产数据，可以实现跨协程生产
    //emit 改为 send 发送数据
    //为什么可以跨协程？？？send在不同的协程，但是receive还是在collect所在的协程
    val channelFlow = channelFlow {
        //回调支持，回调中无法调用挂起函数，使用trySend发送数据
        //awaitClose保证能发送数据，而不是还未回调，协程就执行完成了
        github.contributorsCall("square", "okhttp").enqueue(
            object : Callback<List<Contributor>> {
                override fun onResponse(
                    call: Call<List<Contributor>>,
                    response: Response<List<Contributor>>
                ) {
                    trySend(response.body()!!)
                    close()
                }

                override fun onFailure(call: Call<List<Contributor>>, t: Throwable) {
                    val cancellationException = CancellationException(t.message, t)
                    cancel(cancellationException)
                }
            }
        )
        awaitClose()

//        launch {
//            send(1)
//        }
//        launch {
//            send(2)
//        }
//        launch {
//            send(3)
//        }
    }

    runBlocking {
        launch(Dispatchers.Default) {
            channelFlow.collect {
                println(it)
            }
        }
    }

    //强制调用awaitClose，否则会抛异常
    callbackFlow<List<Contributor>> {
        //回调支持
        github.contributorsCall("square", "okhttp").enqueue(
            object : Callback<List<Contributor>> {
                override fun onResponse(
                    call: Call<List<Contributor>>,
                    response: Response<List<Contributor>>
                ) {
                    trySend(response.body()!!)
                    close()
                }

                override fun onFailure(call: Call<List<Contributor>>, t: Throwable) {

                }
            }
        )
        awaitClose()
    }
}