package com.example.aiyan.basiccoroutines._4_flow

/**
 * distinctUntilChanged：去重，连续相同的数据只会打印一条
 * 默认比较方式：比较key的值
 */

import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.distinctUntilChangedBy
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking

fun main() = runBlocking<Unit> {
    val intFlow = flowOf(1, 2, 3, 3, 4, 4, 5)
    val stringFlow = flowOf("xiaoming", "XiaoMing", "XiaoHong", "xiaohong")
    //通过 == 值判断比较
    intFlow.distinctUntilChanged().collect {
        println("intFlow: $it")
    }
    //自定义比较
    stringFlow.distinctUntilChanged { old, new ->
        old.lowercase() == new.lowercase()
    }.collect {
        println("stringFlow: $it")
    }
    //自定义key
    stringFlow.distinctUntilChangedBy {
        it.uppercase()
    }.collect{
        println("uppercase stringFlow: $it")
    }
}