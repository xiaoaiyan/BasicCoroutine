package com.example.aiyan.basiccoroutines._4_flow

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * flow原理：
 * 设定好一套逻辑，在每个collect的地方重复执行这一套逻辑，而在emit发送数据的节点会替换成collect代码块中的数据处理代码
 *
 * 【flow对象提供数据流的生产逻辑，在collect流程中执行生产逻辑并处理每一条数据】
 *
 * ---- collect：会检查每条数据在发送的时候有没有切换CoroutineContext，flow不允许切换context
 *
 * channel是热的，flow是冷的
 * 数据生产是否独立：channel中send就会生产一条数据，而在flow中只有生产规则，每次collect才会生产（互补干扰）
 *
 * collect相互独立，每次collect都会完整的执行flow中的生产逻辑，所以每次都会收到完整的数据
 *
 * flow调用collect挂起函数后，就会在collect所在的协程执行flow对象中的生产逻辑，生产逻辑中的emit发送数据的节点，
 * 就会执行collect代码块中处理数据的逻辑
 * @sample flowCollect
 *
 * 什么时候需要用到flow？？？
 * 需要连续处理同类型的数据，持续提供数据，拆开数据流的生产和拆开
 *
 * [Flow.collect]：收集数据（事件流一般没有明确的终点，数据流一般有终点）
 * collect是挂起函数，挂起协程，阻塞后面的代码执行，如果一直生产数据，后面的代码会无法执行，所以 ->
 * 放置在单独的协程之中
 */

fun main() = runBlocking<Unit> {
    flowCollect()
    delay(10_000)
}

@OptIn(DelicateCoroutinesApi::class)
private suspend fun flowCollect() {
    val intFlow = flow {
        emit(1)
        delay(1000)
        emit(2)
    }

    GlobalScope.launch {
        intFlow.collect(object : FlowCollector<Int>{
            override suspend fun emit(value: Int) {
                println("coroutine1 value = $value")
            }
        })

        //执行逻辑：
        //println("coroutine1 value = 1")
        //delay(1000)
        //println("coroutine1 value = 2")
    }

    GlobalScope.launch {
        intFlow.collect {
            println("coroutine2 value = $it")
        }
    }
}