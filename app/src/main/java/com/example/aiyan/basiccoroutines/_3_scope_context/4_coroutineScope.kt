package com.example.aiyan.basiccoroutines._3_scope_context

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.supervisorScope

/**
 * coroutineScope可以理解为：就是创建一个子协程在运行（无参数launch，然后join）
 *
 * coroutineScope 和 launch 的区别
 * 1、coroutineScope：没有参数，不能定制coroutineContext，只能沿用父协程的上下文（除了Job）
 * 相当与launch什么参数也不填的效果
 * 2、coroutineScope：挂起函数，「会等启动的协程执行完成后」再继续执行coroutineScope函数，
 * launch协程启动，就执行下一行代码了，不会阻塞
 *
 * 3、coroutineScope有返回值
 *
 * 使用场景：用来在挂起函数中提供CoroutineScope上下文，用来调用launch或者async启动新的协程（在挂起函数中启动协程）
 *
 * 业务封装：抓住协程的异常，避免整个协程树崩溃（因为是挂起函数，协程会等待执行完毕，执行过程出现异常，外部协程可以用try catch捕获到）
 *
 * 父子协程之间是并行的，没法设计处相互try catch的方法
 *
 *
 * supervisorScope：相比于coroutineScope，job类似与SupervisorJob
 */
@OptIn(DelicateCoroutinesApi::class)
fun main() {
//    duration()

    GlobalScope.launch {
        launch {
            println("brother launch in")
            delay(3_000)
            println("brother launch out")
        }
        /*
        catch住异常，只会令coroutineScope中的所有协程取消，不会影响到外面
         */
        try {
            coroutineScope {
                launch {
                    delay(1000)
                    throw RuntimeException("runtime exception")
                }

                launch {
                    println("launch in")
                    delay(3_000)
                    println("launch out")
                }
            }
        } catch (e: Exception) {
            println(e.message)
        }

        /*
        不会影响到兄弟协程而已，也可以像coroutineScope一样，抓住异常
         */
        supervisorScope {
            launch {
                delay(1000)
                throw RuntimeException("runtime exception")
            }

            launch {
                println("launch in")
                delay(3_000)
                println("launch out")
            }
        }
    }

    Thread.sleep(10_000)
}

@OptIn(DelicateCoroutinesApi::class)
private fun duration() {
    GlobalScope.launch {
        val startLaunch = System.currentTimeMillis()
        launch {
            delay(3_000)
        }
        println("launch time: ${System.currentTimeMillis() - startLaunch}")

        val start = System.currentTimeMillis()
        coroutineScope {
            launch {
                delay(3_000)
            }
        }
        println("coroutineScope time: ${System.currentTimeMillis() - start}")
    }

}

/**
 * 在挂起函数中提供CoroutineScope启动协程
 */
private suspend fun suspendFun() = coroutineScope {
    launch {

    }
}

private suspend fun catchException(){

    try {
        coroutineScope {

        }
    } catch (e: Exception) {
        e.printStackTrace()
    }
}