package com.example.aiyan.basiccoroutines._4_flow

import com.example.aiyan.basiccoroutines.cancelMockRetrofit
import com.example.aiyan.basiccoroutines.mockGithub
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.runBlocking

/**
 * catch操作符：捕获上游生产数据时产生的异常，但是emit函数产生的异常不会捕获
 *  ---- emit函数的异常就是下游产生的异常
 *
 * 相当于将上游的生产逻辑代码全部用try catch包住，但是emit中抛出的异常不会捕获，会穿透try catch
 *
 * 只能捕获到上游抛出的异常，下游的异常管不到
 *
 * 特例：catch 不会捕获cancellationException，不管哪里的CancellationException
 *
 * 多个catch时，最上面的catch的捕获它之上的异常，而之后的catch只能捕获两个catch之间抛出的异常
 * 相连的多个catch只有最上面的catch能捕获异常
 *
 *
 * TODO「catch：接管，catch之上的管道已经全部坏了，可以在catch接入新的管道，重新发送数据」
 *
 *
 * catch操作符和try catch的选择
 *
 * 1、try catch在flow内部使用，有可能正确处理异常，修复问题，flow活着
 * 2、catch是已经抛出异常了，flow已经出问题了
 *
 * 当无法修改flow的内部数据生产流程时，使用catch操作符，捕获异常，修复？？？通常只能收尾处理
 * 能在内部使用try catch捕获异常的，使用try catch，捕获异常，保证数据流的正常
 *
 * （使用其他人提供的Flow时，无法修改内部生产流程，只能使用catch操作符，捕获异常，收尾）
 */
fun main() = runBlocking<Unit> {
//    useCatch()

    flow {
        emit(1)
        emit(2)
        emit(3)
        emit(4)
    }.map {
        if (it == 3) throw RuntimeException("cannot handle $it")
        it + it
    }.catch {
        println("happen an exception: $it")
        //接管上游的数据发送
        emit(-1)
    }.collect{
        println("collect: $it")
    }
}

private suspend fun useCatch() {
    flow {
        emit("okhttp")
        emit("retrofit")
        emit("okio")
    }.catch {
        println("exception in catch1 $it")
    }.map {
        if (it == "okio") throw RuntimeException("not support okio")
        "square" to it
    }.catch {
        println("exception in catch2 $it")
    }.onCompletion {
        cancelMockRetrofit()
    }.collect {
        val contributors = try {
            mockGithub.contributors(it.first, it.second)
        } catch (e: Exception) {
            println("exception in collect: $e")
            mutableListOf()
        }
        if (contributors.isNotEmpty())
            println(contributors)
    }
}