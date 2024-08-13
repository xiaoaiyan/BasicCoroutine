package com.example.aiyan.basiccoroutines._4_flow

import com.example.aiyan.basiccoroutines.Contributor
import com.example.aiyan.basiccoroutines.github
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.yield
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.RestrictsSuspension

/**
 * Flow：数据流，同一个协程下的数据序列的持续发送和处理
 * ShardFlow：事件订阅。事件序列向多个订阅者，一对多、跨协程的通知
 * StateFlow：状态订阅
 *
 * flow的定位：协程版的sequence
 *
 * 队列：从头取，从尾放（按照放入的顺序取出数据）
 *
 * [Sequence]：序列。按照顺序提供数据，并且按照提供的顺序获取数据的API
 * 不是数据结构，而是机制，sequence提供的是数据的构建规则，根据规则动态生产数据
 * 生产一条，使用一条，再生产下一条，再使用。。。
 *
 * 注意：：
 * [yield] - 让出执行
 * [SequenceScope.yield] - 生产数据
 *
 * @sample sampleSequence
 *
 * Sequence：只有生产规则，没有保存的内部数据
 *
 * 相比于list，sequence的快：
 * 生产100条数据，sequence是第一条生产完就能开始处理数据了
 * 而list是100条数据生产完才会开始处理数据
 * 但是全部数据处理完，是一样的
 *
 * [SequenceScope] 上有个 [RestrictsSuspension] 注解
 * RestrictsSuspension：注解声明的对象在调用挂起函数时只能是自己的挂起函数，不能是其他挂起函数
 * 所以在SequenceScope的上下文，只能使用yield和yieldAll这两个挂起函数
 *
 *
 * flow定位类似与sequence，但是支持挂起函数（挂起版的sequence）
 *
 * flow：就是边生产边消费的数据流，生产一个消费一个（简单点：就是数据流）
 */

private fun sampleSequence() {
    //创建sequence只是将生产数据的逻辑代码块保存，代码块不会运行
    val sequence = sequence {
        yield(1)
        yield(2)
        yield(3)
        println("sequence end")
    }
    //遍历时才会执行代码块生产数据，用一条生产一条（惰性生产）
    for (data in sequence) {
        println(data)
        if (data == 2) break
    }
}

fun main() = runBlocking<Unit> {

    /**
     * 在我需要运行flow的协程里遍历flow
     *
     * 生产数据：emit
     * 消费数据：collect
     */
    val flow = flow<Int> {
        emit(1)
    }
    flow.collect()
}