package com.example.aiyan.basiccoroutines._4_flow

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * StateFlow：就是replay = 1的SharedFlow
 */
fun main() = runBlocking<Unit> {
    launch(Dispatchers.Default) {
        flow<Int> {
            emit(1)
            delay(100)
            emit(2)
            delay(100)
            emit(3)
        }.stateIn(this).collect{
            delay(200)
            println("collect: $it")
        }
    }

    val stateFlow = MutableStateFlow<Int>(10)

    //隐藏emit，只暴露collect
    stateFlow.asStateFlow()

    flow<Int> {  }.stateIn(this)
    launch(Dispatchers.Default) {
        stateFlow.collect{
            println("collect: $it")
        }
    }
    delay(1_000)
    stateFlow.emit(100)
}