package com.example.aiyan.basiccoroutines._4_flow

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.fold
import kotlinx.coroutines.flow.reduce
import kotlinx.coroutines.flow.runningFold
import kotlinx.coroutines.flow.runningReduce
import kotlinx.coroutines.flow.scan
import kotlinx.coroutines.runBlocking


/**
 * 累计计算：这一轮的值与上一轮的计算结果进行运算
 * reduce：第一轮取前两个元素作为初始值
 * fold：提供初始值
 *
 * reduce、fold 都是末端操作符，不会返回flow，而是直接收集collect，返回计算结果
 */

fun main() = runBlocking<Unit> {
    //reduce：挂起函数，开启collect收集，执行计算逻辑，返回结果
    val reduce = flowOf(1, 2, 3, 4, 5).reduce { accumulator, value ->
        accumulator + value
    }
    println("reduce $reduce")

    //runningReduce：普通函数，返回flow，会将每一轮的计算结果emit给下游
    flowOf(1, 2, 3, 4, 5).runningReduce { accumulator, value ->
        accumulator + value
    }.collect {
        println("runningReduce $it")
    }

    //fold：挂起函数，与reduce类似，返回值类型与初始值一致
    val fold = flowOf(1, 2, 3, 4, 5).fold("accumulator") { accumulator, value ->
        accumulator + value
    }
    println("fold: $fold")

    //runningFold：和runningReduce类似
    flowOf(1, 2, 3, 4, 5).runningFold("accumulator") { accumulator, value ->
        accumulator + value
    }.collect{
        println("runningFold: $it")
    }

    //scan：和runningFold完全一样
    flowOf(1, 2, 3, 4, 5).scan("scan"){accumulator, value -> accumulator + value}.collect{
        println("scan $it")
    }
}