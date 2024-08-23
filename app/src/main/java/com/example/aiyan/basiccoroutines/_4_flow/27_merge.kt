package com.example.aiyan.basiccoroutines._4_flow

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.combineTransform
import kotlinx.coroutines.flow.flatMapConcat
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flatMapMerge
import kotlinx.coroutines.flow.flattenConcat
import kotlinx.coroutines.flow.flattenMerge
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.merge
import kotlinx.coroutines.flow.zip
import kotlinx.coroutines.runBlocking

/**
 * 合并多个flow？？？
 *
 * 原理：通过特定规则，合并成一个flow
 *
 * 生产不同数据类型的flow是否能够合并？？？
 */

@OptIn(ExperimentalCoroutinesApi::class)
fun main() = runBlocking<Unit> {
    iterableFlow()

    flowOfFlow()
}

@OptIn(ExperimentalCoroutinesApi::class)
private suspend fun flowOfFlow() {
    //flow包含flow，即flow的每条数据是flow concat = concatenate
    val stringFlow1 = flow<String> {
        emit("1")
        delay(100)
        emit("2")
        delay(200)
        emit("3")
        delay(300)
    }
    val stringFlow2 = flow<String> {
        emit("4")
        delay(50)
        emit("5")
        delay(150)
        emit("6")
        delay(250)
    }
    //flattenConcat：顺序展开，收集每个flow，发送数据
    flowOf(stringFlow1, stringFlow2).flattenConcat().collect {
        println("flattenConcat: $it")
    }
    //flattenMerge：交替展开，并发数==1时，就是flattenConcat，否则会开启多个协程同时collect多个flow（底层也是ChannelFlow实现）
    flowOf(stringFlow1, stringFlow2).flattenMerge().collect {
        println("flattenMerge: $it")
    }
    //flatMapConcat：相当于先使用map将flow转换成flow(flow)，然后使用flattenConcat展开
    stringFlow1.flatMapConcat { value ->
        flow {
            for (int in 1..value.toInt()) {
                emit("$value - $int")
            }
        }
    }.collect {
        println("flatMapConcat: $it")
    }
    //flatMapMerge：相当于先使用map将flow转换成flow(flow)，然后使用flattenMerge展开
    stringFlow1.flatMapMerge { value ->
        flow {
            for (int in 1..value.toInt()) {
                emit("$value - $int")
            }
        }
    }.collect {
        println("flatMapMerge: $it")
    }
    //flatMapLatest：顺序展开flow，如果上游新的flow产生，则停止之前的，开始收集新的
    flow<String> {
        emit("1")
        delay(100)
        emit("2")
        delay(200)
        emit("3")
        delay(300)
        emit("4")
    }.flatMapLatest { value ->
        flow {
            for (int in 1..10) {
                emit("$value - $int")
                delay(50)
            }
        }
    }.collect {
        println("flatMapLatest: $it")
    }
    //combine：每次收到flow的数据时，运行组合逻辑，将各个flow当前的数据组合
    stringFlow1.combine(stringFlow2) { flow1, flow2 ->
        "$flow1 - $flow2"
    }.collect {
        println("combine: $it")
    }
    /* 运行
    combine: 1 - 4
    combine: 1 - 5
    combine: 2 - 5
    combine: 2 - 6
    combine: 3 - 6
     */

    //combineTransform：相比于combine，组合数据后需要自己emit
    stringFlow1.combineTransform(stringFlow2){flow1, flow2 ->
        emit("$flow1 - $flow2")
    }.collect{
        println("combineTransform: $it")
    }

    //zip：相比于combine，使用过的元素不在使用
    stringFlow1.zip(stringFlow2) { flow1, flow2 ->
        "$flow1 - $flow2"
    }.collect {
        println("zip: $it")
    }
}

private suspend fun iterableFlow() {
    val intFlow1 = flowOf(1, 2, 3)
    val intFlow2 = flowOf(4, 5, 6)

    //merge：遍历所有的flow，开启协程，执行所有flow的collect，将数据发送到channel
    merge(intFlow1, intFlow2).collect {
        println("it: $it")
    }
    //集合包含多个flow才能使用merge合并
    listOf(intFlow1, intFlow2).merge().collect {
        println("listOf $it")
    }
    setOf(intFlow1, intFlow2).merge().collect {
        println("setOf $it")
    }
}