package com.example.aiyan.basiccoroutines._4_flow

import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.sample
import kotlinx.coroutines.flow.timeout
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * 时间相关
 *
 * timeout：collect之后发送数据要在时间间隔之内，并且之后的每条数据
 * 的发送间隔都要在规定时间之内，否则抛出异常TimeoutCancellationException
 * （FlowPreview注解：预览api，可能不会向后兼容）
 *
 * sample：规定时间间隔发送数据，时间间隔内收到多个数据保留最新的发送
 *  ---- 最后发送的数据，如果没到间隔节点，不会发送
 *
 *  debounce：设置等待时长，接收数据，开始等待，超过等待时间，发送数据，等待时间内
 *  有新的数据接收到，开启新的等待（特定时间内没有新的接收，则发送数据）
 *  ----数据流结束，发送最后处于等待的数据
 */

@OptIn(FlowPreview::class)
fun main() = runBlocking<Unit> {
    try {
        flow {
            emit(1)
            delay(500)
            emit(2)
            delay(500)
            emit(3)
            delay(1000)
            emit(4)
            delay(1500)
            emit(5)
        }.timeout(1.seconds).collect {
            println("timeout $it")
        }
    } catch (e: Exception) {
        println(e)
    }

    /*
    0 - 1s，收到1
    1s - 2s，收到2，3，发送3
    2s - 3s，收到4，2.5s数据流结束，4不会发出
     */
    flow {
        delay(500)
        emit(1)
        delay(500)
        emit(2)
        emit(3)
        delay(1000)
        emit(4)
        delay(500)
    }.sample(1.seconds).collect {
        println("sample $it")
    }


    flow {
        emit(1)
        delay(500)
        emit(2)
        delay(500)
        emit(3)
        delay(500)
        emit(4)
        delay(500)
        emit(5)
        emit(6)
    }.debounce(1.seconds).collect {
        println("debounce $it")
    }
}

/**
 * 屏幕点击防抖动效果
 */
fun <T> Flow<T>.throttle(duration: Duration) = flow<T> {
    var lastTime = 0L
    collect {
        if (System.currentTimeMillis() - lastTime > duration.inWholeMilliseconds) {
            emit(it)
            lastTime = System.currentTimeMillis()
        }
    }
}