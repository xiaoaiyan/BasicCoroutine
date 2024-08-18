package com.example.aiyan.basiccoroutines._4_flow

import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.retry
import kotlinx.coroutines.flow.retryWhen
import kotlinx.coroutines.runBlocking

/**
 * retry：重试，上游抛出异常时，先捕获，retry触发，调用上游Flow的collect，重启数据生产流程（整个生产链条）
 *
 * 和catch类似，只有上游抛出异常时
 *
 * TODO catch：接管生产 retry：重启上游，重新生产
 */

fun main() = runBlocking<Unit> {
    //retry() -> 一直重启
    //retry(int) -> 设置重试次数，达到重试次数，抛出异常
    //retry(int){boolean} -> 双重限制条件，次数和block返回值限制，true才会重试
    //retryWhen{cause, attempt -> boolean} catch 异常类型，attempt已经尝试次数（第一次到达为0）
    try {
        flow {
            emit(1)
            emit(2)
            throw RuntimeException("--------")
            emit(3)
            emit(4)
            emit(5)
        }.map {
            it * it
        }.retry(3){
            it is RuntimeException
        }.retryWhen { cause, attempt ->
            attempt < 3
        }.collect{
            println("collect $it")
        }
    } catch (e: Exception) {
        println("catch exception $e in launch")
    }
}