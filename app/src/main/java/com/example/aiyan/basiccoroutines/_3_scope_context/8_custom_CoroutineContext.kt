package com.example.aiyan.basiccoroutines._3_scope_context

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.AbstractCoroutineContextElement
import kotlin.coroutines.CoroutineContext

/**
 * 自定义协程上下文：继承AbstractCoroutineContextElement
 */

fun main() {
    runBlocking {
        launch(CustomKey(this)) {
            println(coroutineContext[CustomKey])
        }
    }
}


private class CustomKey(parent: CoroutineScope): AbstractCoroutineContextElement(CustomKey) {
    companion object Key: CoroutineContext.Key<CustomKey>

    override fun toString() = "CustomKey"
}