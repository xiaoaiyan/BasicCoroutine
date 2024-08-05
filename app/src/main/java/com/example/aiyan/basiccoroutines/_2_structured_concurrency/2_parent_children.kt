package com.example.aiyan.basiccoroutines._2_structured_concurrency

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * 父子协程关系的确定
 * 1、launch或者async时指定了Job，启动的协程就是Job的子协程
 * 2、launch或者async时未指定了Job，启动的协程就是使用的CoroutineScope的子协程
 *
 * 结构化结束：父协程需等到所有的子协程都执行结束后才会结束（就算父协程中的逻辑已经执行完毕）
 */
@OptIn(ExperimentalCoroutinesApi::class)
fun main() {
    val start = System.currentTimeMillis()
    runBlocking {
        val coroutine1 = launch() {
            println(
                """
                    child coroutine is ${coroutineContext[Job]}
                    parent coroutine is ${coroutineContext[Job]!!.parent}
                    -----------------------------------------------------
                """.trimIndent()
            )
            delay(3_000)
        }
        async() {
            println(
                """
                    child coroutine is ${coroutineContext[Job]}
                    parent coroutine is ${coroutineContext[Job]!!.parent}
                    -----------------------------------------------------
                """.trimIndent()
            )
        }

        //由于在launch中指定了父协程Job，启动的子协程不是this父协程的子协程，而是Job()的子协程
        launch(Job()) {
            println(
                """
                    child coroutine is ${coroutineContext[Job]}
                    parent coroutine is ${coroutineContext[Job]!!.parent}
                    -----------------------------------------------------
                """.trimIndent()
            )
            delay(1_000)
        }
        println(
            """
                parent coroutine: ${coroutineContext[Job]}
                children count: ${coroutineContext[Job]!!.children.count()}
                children list: ${coroutineContext[Job]!!.children.toList()}
                -----------------------------------------------------
            """.trimIndent()
        )
    }

    //由于coroutine1需要延时3s才能完成，所以整个协程完成的时间大致为3s（父协程会等待子协程执行完毕再结束）
    println("duration is ${System.currentTimeMillis() - start}")
}
