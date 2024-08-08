package com.example.aiyan.basiccoroutines.reflections

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * TODO -----------------------异常流程----------------------
 * 1、抛出异常的协程，取消自己，isActive = false，协程链中的其他协程，isActive也会置为false
 * 正在挂起的协程，抛出CancellationException取消
 *
 * 2、CoroutineExceptionHandler中的context是最外层协程的上下文
 */
class StructuredConcurrencyActivity: ComponentActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch(CoroutineExceptionHandler{context, exception ->
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