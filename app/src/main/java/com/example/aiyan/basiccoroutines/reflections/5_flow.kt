package com.example.aiyan.basiccoroutines.reflections

import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration
import kotlin.time.Duration.Companion.milliseconds

private fun <T> Flow<T>.throttle(timeWindow: Duration) = flow<T> {
    var lastTime: Long = 0
    collect{
        if (System.currentTimeMillis() - lastTime > timeWindow.inWholeMilliseconds){
            emit(it)
            lastTime = System.currentTimeMillis()
        }
    }
}

fun main() {
    val flow = flow<Int> {
        emit(1)
        delay(100)
        emit(2)
        delay(200)
        emit(3)
        delay(300)
        emit(4)
        delay(400)
        emit(5)
    }
    runBlocking {
        flow.throttle(500.milliseconds).collect{
            println(it)
        }
    }
}