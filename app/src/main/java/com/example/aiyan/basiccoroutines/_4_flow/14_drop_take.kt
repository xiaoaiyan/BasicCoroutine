package com.example.aiyan.basiccoroutines._4_flow

import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.dropWhile
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.runBlocking

fun main() = runBlocking<Unit> {
    //drop：丢弃前几条数据
    flowOf(1, 2, 3, 4, 5).drop(2).collect {
        println("drop: $it")
    }
    //输出：
    //dropped: 3
    //dropped: 4
    //dropped: 5

    //dropWhile：丢弃满足条件的数据，一旦开始不符合条件，后续的数据都会发送
    //（从不符合开始，后续的都不会检查，直接发送）
    flowOf(1, 2, 3, 4, 5).dropWhile {
        it < 3
    }.collect {
        println("dropWhile: $it")
    }
    //输出：
    //dropWhile: 3
    //dropWhile: 4
    //dropWhile: 5

    //take：取前几条数据
    flowOf(1, 2, 3, 4, 5).take(2).collect{
        println("take: $it")
    }
    //输出
    //take: 1
    //take: 2

    //takeWhile：取满足条件的数据，一旦不满足条件，结束flow
    flowOf(1, 2, 3, 4, 5).takeWhile {
        it < 3
    }.collect{
        println("takeWhile: $it")
    }
    //输出
    //takeWhile: 1
    //takeWhile: 2
}