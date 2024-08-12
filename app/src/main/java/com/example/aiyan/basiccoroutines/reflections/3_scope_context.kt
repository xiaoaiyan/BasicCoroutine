package com.example.aiyan.basiccoroutines.reflections

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.NonCancellable
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume

/**
 * [NonCancellable]：单例Job，断开协程链，当协程isActive为false时也能正常执行挂起函数（用来执行收尾工作）
 * [SupervisorJob]：子协程异常取消影响不到父协程，父协程取消子协程也会取消（断开异常流程）- 子协程有Job
 * [GlobalScope]：Job为null，启动的协程链没有父Job，多个协程链之间异常不会相互影响 - 子协程没Job
 */
class ScopeContextActivity: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            val result = suspendCancellableCoroutine<String> {
                CoroutineScope(it.context).launch {
                    delay(3000)
                    it.resume("success")
                }
            }
            println(result)
        }
    }

    private suspend fun suspendFunctionInfo(){
        currentCoroutineContext().printInfo("suspend")
    }
}

private fun CoroutineContext.printInfo(key: String){
    println("$key ---- context: $this")
    println("$key ---- job: ${this[Job]}")
    println("$key ---- dispatcher: ${this[ContinuationInterceptor]}")
}