package com.example.aiyan.basiccoroutines._4_flow

import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEmpty
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.runBlocking

/**
 * 流程监听onStart onCompletion onError
 *
 * onStart：收集流程开始之前执行，collect函数被调用之后，调用上游的collect生产之前执行
 * 连续调用时，下面的onStart会先触发
 * TODO 上游try catch，即使emit也被包住，也无法捕获onStart中抛出的异常，但是使用catch操作符能够捕获
 *
 * onComplement：数据流完成就会触发，正常结束和异常结束都会触发，并且不会拦截异常
 * catch操作符是有异常时才会触发，并且会捕获异常
 *
 * onEmpty：正常结束，并且没有一条数据时触发
 */

fun main() = runBlocking<Unit> {
    flowOf(1, 3, 5, 7, 9)
        .onStart {
            emit(-1)
        }.onCompletion {

        }.onEmpty {

        }.collect{
            println("collect $it")
        }
}