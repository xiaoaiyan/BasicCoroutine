package com.example.aiyan.basiccoroutines._4_flow

import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterIsInstance
import kotlinx.coroutines.flow.filterNot
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.runBlocking

/**
 * Flow操作符：用一个或者多个flow对象，创建出新的flow对象
 * ----经过操作符后返回新的Flow对象，原对象不变
 *
 * filter系列：条件过滤
 *
 *
 */

fun main() = runBlocking<Unit> {
    val flowOf = flowOf(
        1, 2, null, 3, 4, 5, null
    )
    //filter：保留满足条件的元素
    flowOf.filter {
        it?.and(1) == 0 // 是否为偶数
    }.collect {
        println("filter $it")
    }

    //filterNot：保留不满足条件的元素
    flowOf.filterNot {
        it?.and(1) == 0 // 是否为奇数
    }.collect {
        println("filterNot $it")
    }

    //filterNotNull：保留不为null的元素
    //排除null值，filterNotNull很方便
    flowOf.filterNotNull().collect() {
        println("filterNotNull $it")
    }

    //filterIsInstance：保留指定类型的元素
    val anyFlow =
        flowOf(1, 2, 3, "success", "failure", listOf(1, 2, 3), listOf("success", "failure"))
    anyFlow.filterIsInstance<Int>().collect() {
        println("filterIsInstance $it")
    }
    anyFlow.filterIsInstance(String::class).collect() {
        println("filterIsInstance $it")
    }
    //具体范型类型过滤
    anyFlow.filter { any ->
        any is List<*> && any.firstOrNull()?.let { it is String }?: false
    }.collect() {
        println("filterIsInstance $it")
    }
}