package com.example.aiyan.basiccoroutines._1_basics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.aiyan.basiccoroutines.Contributor
import com.example.aiyan.basiccoroutines.github
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RunBlockingActivity() : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * runBlocking启动协程，和launch和async的区别
         * 1、不需要CoroutineScope（不需要上下文，不需要被取消）
         * 2、阻塞线程
         *
         * runBlocking取消会抛异常
         *
         * CoroutineScope：包含协程需要用到的上下文coroutineContext 和 取消所有启动的协程树的方法  cancel()
         *
         * 主线程通过launch和async启动协程，ContinuationInterceptor为Dispatchers.Main.immediate时，会比Dispatchers.Main先执行
         *
         * CoroutineScope == BlockingCoroutine
         * Job == BlockingCoroutine
         * ContinuationInterceptor == BlockingEventLoop
         */

        lifecycleScope.launch(Dispatchers.Main) {
            println("in Dispatchers.Main launch")
        }

        /**
         * 启动协程后，立即在主线程上执行协程代码
         */
        lifecycleScope.launch(Dispatchers.Main.immediate) {
            println("in Dispatchers.Main.immediate launch")
        }

        github.contributorsCall("Jetbrains", "kotlin")
            .enqueue(object : Callback<List<Contributor>> {
                override fun onResponse(
                    call: Call<List<Contributor>>,
                    response: Response<List<Contributor>>
                ) {
                    /**
                     * 将协程代码变成阻塞式，在线程API中调用
                     * 阻塞当前线程，执行协程代码，执行完毕后，才会放开当前线程，开始执行下一行代码
                     *
                     * 场景：已经使用挂起函数实现了某个功能，线程环境下调用后需要函数的返回结果
                     */
                    val avatars = runBlocking {
                        handleData(response.body()!!)
                    }
                    println(avatars)
                }

                override fun onFailure(call: Call<List<Contributor>>, throwable: Throwable) {
                    TODO("Not yet implemented")
                }
            })
    }


    private suspend fun handleData(contributors: List<Contributor>) =
        withContext(Dispatchers.Default) {
            contributors.map { it.avatar_url }
        }
}