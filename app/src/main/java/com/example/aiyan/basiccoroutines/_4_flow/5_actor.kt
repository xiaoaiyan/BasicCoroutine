package com.example.aiyan.basiccoroutines._4_flow

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.ObsoleteCoroutinesApi
import kotlinx.coroutines.channels.actor
import kotlinx.coroutines.channels.produce
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * actor：将消息的接收和创建合并在一起，返回SendChannel
 *
 * ObsoleteCoroutinesApi：在未来可能会被抛弃的API
 */

@OptIn(ObsoleteCoroutinesApi::class, ExperimentalCoroutinesApi::class)
fun main() = runBlocking<Unit> {
    val sendChannel = actor<Int> {
        for (data in this){
            println("receive $data")
        }
    }
    launch {
        var value = 1
        while (isActive){
            sendChannel.send(value++)
            if (value == 16) return@launch
        }
    }

    val receiveChannel = produce<Int> {
        send(1)
    }
}