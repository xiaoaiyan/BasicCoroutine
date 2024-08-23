package com.example.aiyan.basiccoroutines._4_flow

import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.produceIn
import kotlinx.coroutines.flow.single
import kotlinx.coroutines.flow.singleOrNull
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking

private val flow = flowOf(1, 2, 3, 4, 5)

/**
 * 将flow对象转换为其他类型
 *
 * 原理：收集flow元素，收集过程中运算，返回结果
 *
 * terminal operator：直接收集，不返回flow的操作符
 */

fun main() = runBlocking<Unit> {
    //first：挂起函数，得到数据流的第一条数据（找到就会返回，没有就会抛出NoSuchElementException）
    val intFlow = flowOf(1, 2, 3, 4, 5)
    val first1 = intFlow.first()
    println("first: $first1")
    //first{}：找到符合条件的第一个元素
    val first2 = intFlow.first {
        it > 3
    }
    println("first: $first2")
    //firstOrNull，firstOrNull{}，类似与first和first{}，只是没有找到时，返回NULL而不是抛出异常
    intFlow.firstOrNull()
    intFlow.firstOrNull { it > 3 }
    //single：只有一个元素时返回
    //否则抛出异常（没有元素NoSuchElementException，多于一个，IllegalArgumentException）
    //singleOrNull：类似single，只是不符合会返回NULL
    intFlow.single()
    intFlow.singleOrNull()
    //count：计算满足条件的数据个数
    intFlow.count()
    intFlow.count {
        it > 3
    }
    //flow转换为List：收集flow，依次添加到List中
    intFlow.toList()
    intFlow.toList(mutableListOf())
    //flow转换为Channel（produce开启协程，emit通过channel send发送到channel）
    val receiveChannel = intFlow.produceIn(this)
    for (value in receiveChannel){
        println("value: $value")
    }
}