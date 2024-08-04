package com.example.aiyan.basiccoroutines.reflections

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.EmptyCoroutineContext

fun main() = runBlocking {
    val coroutineScope = CoroutineScope(EmptyCoroutineContext)
    coroutineScope.launch {
        try {
            throw NullPointerException("Test")
        } catch (e: Exception) {
            println(e.message)
        }
    }
    delay(10_000)
}