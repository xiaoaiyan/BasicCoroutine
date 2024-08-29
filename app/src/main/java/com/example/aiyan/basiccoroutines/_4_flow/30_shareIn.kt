package com.example.aiyan.basiccoroutines._4_flow

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * shareIn()的参数
 *
 * 1、scope: CoroutineScope
 * 2、started: SharingStarted,
 * 3、replay: Int = 0
 *
 * scope：提供启动协程的CoroutineScope，并启动协程开启收集数据
 * replay：缓冲和缓存，容量为reply（上游发送过快时，缓冲上游的数据数据发送完毕后，缓存已经发送的数据）
 * started：启动方式
 * ---- SharingStarted.Eagerly：马上启动协程开启收集
 * ---- SharingStarted.Lazily：第一次collect时，启动协程收集
 * ---- SharingStarted.WhileSubscribed：制定数据流程的结束和重启的规则
 * 第一次订阅时启动上游的数据流，而且在下游所有的订阅都结束之后，就会关闭上游的生产过程。而之后有新的订阅时会重启整个生产过程
 *      ---- stopTimeoutMillis：SharedFlow结束延时时间，在所有的收集者结束之后，间隔stopTimeoutMillis时间后，SharedFlow结束
 *          默认为0，就是所有收集者结束收集，SharedFlow马上结束
 *      ---- replyExpirationMillis：flow结束后，reply缓存保存的时间，期间再次启动flow，会先发送缓存数据
 *
 *
 * TODO SharedFlow的订阅（调用collect后）就会一直处于无限循环的状态，不像Flow一样随着上游数据发送结束，collect就会返回
 * TODO 如何结束订阅呢？？？取消collect所在的协程
 *
 * shareIn：默认的buffer大小：64（replay + extraBufferCapacity）
 */

@OptIn(DelicateCoroutinesApi::class, ExperimentalCoroutinesApi::class)
fun main() = runBlocking<Unit> {
    //runBlocking的子协程但是运行在Dispatchers.Default
    GlobalScope.launch(coroutineContext[Job]!!) {
        //Flow完成发送需要5s重
        val flow = flow {
            emit(1)
            delay(500)
            emit(2)
            delay(1000)
            emit(3)
            delay(1500)
            emit(4)
            delay(2000)
            emit(5)
        }

//        //自己的shareIn
//        val sharedFlow = flow.myShareIn()
//        launch {
//            delay(2000)
//            sharedFlow.collect {
//                println("collect $it")
//            }
//        }

        //上游Flow会运行在this scope启动的子协程之中
        val sharedFlow = flow.shareIn(this, SharingStarted.WhileSubscribed())
        //SharedFlow的collect不会在上游数据发送完毕后就自动结束，需要手动取消collect所在的协程才会结束
        val launch = launch {
            sharedFlow.collect {
                println("SharedFlow collect: $it")
            }
        }
        launch {
            delay(6000)
            sharedFlow.collect{
                println("SharedFlow collect after 6s: $it")
            }
        }
        delay(5000)
        launch.cancel()
    }
}

@OptIn(DelicateCoroutinesApi::class)
private fun <T> Flow<T>.myShareIn(): SharedFlow<T> {
    val mutableSharedFlow = MutableSharedFlow<T>()
    GlobalScope.launch {
        collect {
            mutableSharedFlow.emit(it)
        }
    }
    return mutableSharedFlow
}