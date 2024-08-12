package com.example.aiyan.basiccoroutines.reflections

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * TODO ---------------协程中的Job和挂起函数中的Job -----------
 * 挂起函数如果不创建新的Job，会使用和调用协程环境同样的Job
 * withContext，coroutineScope，supervisorScope会创建新的Job
 *
 * TODO -----------------------异常流程----------------------
 * 1、抛出异常的协程（如果异常被try catch则不会），取消自己，isActive = false，协程链中的其他协程，isActive也会置为false
 * 正在挂起的协程，抛出CancellationException取消
 *
 * 2、CoroutineExceptionHandler中的context是最外层协程的上下文
 *
 *
 * TODO 源码分析？？？
 */
class StructuredConcurrencyActivity: ComponentActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        structuralException()

        val coroutineScope = CoroutineScope(SupervisorJob())
        coroutineScope.launch {
            println("default coroutineContext in")
            delay(3_000)
            println("default coroutineContext out")
        }

        coroutineScope.launch(CoroutineExceptionHandler{_, exception ->
            println("exception: $exception")
        }){
            launch {
                delay(1_000)
                throw RuntimeException("自己抛出的异常")
            }
        }
    }

    private fun structuralException() {
        lifecycleScope.launch(CoroutineExceptionHandler { context, exception ->
            println("coroutine exception: $exception")
            println("coroutine exception context: $context")
        }) {
            launch {
                launch {
                    try {
                        delay(3000)
                    } catch (exception: Exception) {
                        println("brother coroutine exception $exception")
                        throw exception
                    }
                }

                launch {
                    println("exception coroutineContext: $coroutineContext")
                    delay(1_000)
                    throw NullPointerException()
                }

                try {
                    delay(3000)
                } catch (exception: Exception) {
                    println("father coroutine exception $exception")
                    throw exception
                }
            }


            println("grand parent coroutineContext: $coroutineContext")
            try {
                delay(10_000)
            } catch (exception: Exception) {
                println("grand parent exception $exception")
            }
        }
    }
}