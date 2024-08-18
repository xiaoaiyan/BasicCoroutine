package com.example.aiyan.basiccoroutines._4_flow

import com.example.aiyan.basiccoroutines.mockGithub
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking

/**
 * Exception Transparency：异常的可见行
 * ----Flow 上游生产数据不应捕获下游的异常
 * 上游try catch 包含 emit时，下游的异常就会被上游拦截（异常是沿着从上游到下游，每个emit函数传递的）
 * 就是说：try catch collect函数无法捕获到异常
 *
 *
 * flow的生产：就是创建flow时传入的函数类型对象「flow(block: suspend FlowCollector<T>.() -> Unit)」
 *      ----也就是flow block代码块中的逻辑
 * collect函数会传入FlowCollector对象，执行block代码块生产数据
 * 调用emit函数：就是调用collect时传入的FlowCollector的emit函数，就是执行collect大括号中的逻辑
 *
 *
 * flow的代码都是在collect之下触发，所以如果异常抛到了collect函数中，没有捕获，数据流就结束了
 *
 *
 * Flow：collect函数
 * FlowCollector：emit函数
 * flow{} ----> 创建flow对象，其中隐式的receive FlowCollector，有collect传入的FlowCollector对象封装而来
 * 上游生产Flow对象，collect
 * 下游传入FlowCollector对象，emit
 *
 * flow{} -> map{} -> collect{}流程分析：
 * ---- flow{ emit } -> SafeFlow(block)
 * ---- map{ transform } -> MapFlow(block)
 * ---- collect{} -> collect(FlowCollector)
 *
 * //1 - 可以捕获异常
 * collect(flowCollector1)
 * {
 *      val flowCollector2 = { value ->
 *          //4 - map转换
 *          val map = map(value)
 *          //5 - collect代码块，可以捕获异常
 *          flowCollector1.emit(map)
 *      }
 *
 *      //2（无法捕获，操作符内部）
 *      collect(flowCollector2)
 *      {
 *          //最上游flow的生产block
 *          {
 *              //3 - 最上游的emit，可以捕获异常
 *              flowCollector2.emit(it)
 *          }
 *      }
 * }
 *             4
 * 1 - 2 - 3 - 5
 * 3捕获异常，1就无法捕获到了，除非3再次抛出异常
 * 4处抛出的异常，会向上到3，1
 * 5处抛出的异常，不会经过4
 *
 * (上游Flow对象调用collect，传入FlowCollector对象)
 */

fun main() = runBlocking<Unit> {
    try {
        flow {
            emit("okhttp")
            emit("retrofit")
            try {
                emit("okio")
            } catch (e: Exception) {
                println("flow catch $e")
            }
        }.map {
            //异常会向上，通过emit，向上游抛出
            if (it == "okio") throw RuntimeException("not support okio")
            "square" to it
        }.collect {
            val contributors = mockGithub.contributors(it.first, it.second)
            println(contributors)
        }
    } catch (e: Exception) {
        println("collect catch $e")
    }

//    flowThinking()
}

private suspend fun flowThinking(){
    /*
    创建flow对象，创建flowCollector对象，调用flow.collect传入flowCollector，在collect方法中
    调用flowCollector.emit方法，就从collect到了emit
     */
    val flow = object: Flow<Int>{
        override suspend fun collect(collector: FlowCollector<Int>) {
            collector.emit(1)
            collector.emit(2)
        }
    }
    val flowCollector = object : FlowCollector<Int>{
        override suspend fun emit(value: Int) {
            println("value = $value, this = $this")
        }
    }
    flow.collect(flowCollector)
}

private fun <T> Flow<T>.noThing(): Flow<T> = flow {
    //将下游传入的FlowCollector，传入到上游Flow对象的block代码块的隐式接收者中
    println("nothing execute")
    collect(this)
}

private fun <T> noThing(flow: Flow<T>): Flow<T> = object : Flow<T>{
    override suspend fun collect(collector: FlowCollector<T>) {
        println("nothing execute")
        flow.collect(collector)
    }
}

private fun <T> map(flow: Flow<T>, block: (value: T) -> T): Flow<T> = object : Flow<T>{
    override suspend fun collect(collector: FlowCollector<T>) {
        val flowCollector = object : FlowCollector<T>{
            override suspend fun emit(value: T) {
                collector.emit(block(value))
            }
        }
        flow.collect(flowCollector)
    }
}