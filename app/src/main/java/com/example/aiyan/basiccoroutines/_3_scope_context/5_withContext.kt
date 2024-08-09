package com.example.aiyan.basiccoroutines._3_scope_context

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * withContext：
 * 1、相比于coroutineScope，withContext可以填CoroutineContext
 * 底层依然会通过开启子协程实现
 * 但是上层业务就可以看成当前协程上下文切换
 *
 * 2、相当于串行的launch和async
 *
 * coroutineScope完全等价于withContext(coroutineContext){}
 * 或者withContext(EmptyCoroutineContext){}
 *
 * 和launch的关键区别：串行的，用来启动串行的协程
 *
 * 思路：我想切换CoroutineContext，但是不想使用launch和async开启新的协程
 */

@OptIn(DelicateCoroutinesApi::class)
fun main() {
    GlobalScope.launch {
        val parentJob = coroutineContext.job
        launch {
            delay(1000)
        }

        async {
            delay(1000)
        }

        coroutineScope {
            withContext(parentJob){
                println("children count: ${parentJob.children.count()}")
            }
        }
    }

    Thread.sleep(10_000)
}