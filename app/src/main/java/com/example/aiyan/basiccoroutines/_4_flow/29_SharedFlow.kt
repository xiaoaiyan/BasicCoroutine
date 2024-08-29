package com.example.aiyan.basiccoroutines._4_flow

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * SharedFlow：Flow的变种，事件流
 *
 * TODO「Flow调用collect之后才会开启生产数据，创建Flow对象时只是在指定生产数据的逻辑」
 */

fun main() = runBlocking<Unit> {

    sharedFlow()

//    customTimer()
}

/**
 * shareIn：将flow转换为sharedFlow，flow在sharedFlow的上游生产数据
 * shareIn(coroutineScope, SharingStarted.Eagerly)：
 * ---- 指定coroutineScope开启协程，调用flow的collect函数开启数据流
 * ---- 收集到数据后转发给SharedFlow的FloeCollector
 *
 * 冷还是热？？？
 * 调用shareIn后flow就开启了上游数据的发送，可以理解为，而SharedFlow调用collect才会消费数据
 * 可以说，SharedFlow是热流，flow是冷流
 *
 * 但是，SharedFlow只有调用collect之后，才会开始生产数据发送到下游
 * 只是SharedFlow的生产数据只是将Flow的数据流进行转发给自己的FlowCollector而已
 * 而Flow在shareIn时就开始了生产数据
 *
 * shareIn和launchIn的区别
 * 1、都会在指定的CoroutineContext中开启协程，调用collect进行数据收集
 * 2、shareIn会有返回值SharedFlow，SharedFlow会拿到上游被启动的Flow生产的数据，
 * 转发给自己的FlowCollector，而launchIn没有返回值（SharedFlow使用了Flow的生产数据流程）
 * 3、shareIn可以配置启动的时机，马上启动或者稍后启动，而launchIn会马上启动协程开始收集
 *
 * 相比于Channel，collect之后，SharedFlow的事件会发送到它所有的FlowCollector，
 * 但是collect之前产生的事件不会收到（SharedFlow会漏事件），而channel是receive瓜分数据
 *
 * shareIn使用场景（将Flow转换为SharedFlow）：
 * 1、多次收集时共享相同的一次数据生产流程
 * 2、数据生产提前启动（在shareIn时就开始生产）
 *
 * 根本需求：流程的分拆（生产流程和收集流程分开） - 漏数据
 *
 * 不需要从头开始收集数据？？？事件订阅
 */
@OptIn(DelicateCoroutinesApi::class)
private suspend fun sharedFlow() = coroutineScope {
    val emitDataFlow = emitData(5)
    val sharedFlow = emitDataFlow.shareIn(GlobalScope, SharingStarted.Eagerly)
    /**
     * emitDataFlow 和 sharedFlow（Flow和SharedFlow比较）
     * 同样都是延时1000ms后开启收集
     * emitDataFlow能收收集到1-5完整的数据（flow调用collect才会开始生产数据）
     * sharedFlow却只能收集到5（SharedFlow的上游Flow在调用shareIn后就开始了生产数据）
     *
     * TODO 使用SharedFlow考虑下collect时数据是否已经生产完了呀呀呀
     *
     * 判断flow冷热还需要判断是否依靠动态的数据流？？？（怎么说呢，从技术上看，SharedFlow是冷的）
     */
    launch(Dispatchers.Default) {
        delay(1000)
        launch {
            emitDataFlow.collect {
                println("emitDataFlow in coroutine1: $it")
            }
        }
        launch {
            sharedFlow.collect {
                println("sharedFlow in coroutine1: $it")
            }
        }
    }
}

/**
 * Flow的冷和热？？？
 */
private suspend fun customTimer() = coroutineScope {
    Timer.start()

    /**
     * 这个Flow是冷的还是热的？？？
     *
     * 没有调用collect时不会启动，是冷的
     * 但是在不同的时候去调用collect时，发送的数据不一样
     *
     * 调用collect时才会启动生产流程，但是Flow取的数据是独立的生产流程（SharedFlow）
     */
    val flow = callbackFlow {
        Timer.subscribe {
            trySend(it)
        }
        awaitClose()
    }
    launch(Dispatchers.Default) {
        delay(3_000)
        flow.collect {
            println("collect1 $it")
        }
    }
    launch(Dispatchers.Default) {
        delay(5_000)
        flow.collect {
            println("collect2 $it")
        }
    }
}

private fun emitData(num: Int) = flow<Int> {
    for (data in 1..num) {
        emit(data)
        println("emit data $data coroutineContext: ${currentCoroutineContext()}")
        delay(data * 100L)
    }
}

object Timer {
    private var value: Int = 0
        set(value) {
            field = value
            subscribes.forEach {
                it(value)
            }
        }

    private val subscribes = mutableListOf<(Int) -> Unit>()

    fun subscribe(block: (Int) -> Unit) {
        subscribes += block
    }

    @OptIn(DelicateCoroutinesApi::class)
    fun start() {
        GlobalScope.launch {
            while (true) {
                value++
                delay(500)
            }
        }
    }
}