package com.example.aiyan.basiccoroutines.reflections

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

fun main() = runBlocking<Unit> {
    launch(Dispatchers.Default) {
        subscribe {
            println("subscribe1 $it")
        }
    }
    launch(Dispatchers.Default) {
        subscribe {
            println("subscribe2 $it")
        }
    }
    launch(Dispatchers.Default) {
        var value = 0
//        while (true) {
//            postEvent(Event((value++).toString()))
//            delay(1_000)
//        }
        for (index in 1 .. 5){
            postEvent(Event((value++).toString()))
            delay(1_000)
        }
    }
}

data class Event(val event: String)

private val multiSharedFlow = MutableSharedFlow<Event>()

suspend fun subscribe(block: (Event) -> Unit) = coroutineScope {
    launch(NonCancellable) {
        multiSharedFlow.collect {
            println("collect coroutine $coroutineContext")
            block.invoke(it)
        }
    }
}

suspend fun postEvent(event: Event) {
    println("post event ${currentCoroutineContext()}")
    multiSharedFlow.emit(event)
}

