package com.example.aiyan.basiccoroutines._1_basics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.aiyan.basiccoroutines.Contributor
import com.example.aiyan.basiccoroutines.CoroutineApplication
import com.example.aiyan.basiccoroutines.github
import com.example.aiyan.basiccoroutines.mockGithub
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class CallbackToSuspendActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {

            val retrofitJob = launch {
                try {
                    CoroutineApplication.initJob?.join()
                    val retrofit = suspendCoroutine {
                        mockGithub.contributorsCall("square", "retrofit")
                            .enqueue(object : Callback<List<Contributor>> {
                                override fun onResponse(
                                    call: Call<List<Contributor>>,
                                    response: Response<List<Contributor>>
                                ) {
                                    it.resume(response.body()!!)
                                }

                                override fun onFailure(
                                    call: Call<List<Contributor>>,
                                    throwable: Throwable
                                ) {
                                    it.resumeWithException(throwable)
                                }
                            })
                    }
                    println("suspendCoroutine: $retrofit")
                } catch (e: Exception) {
                    println("suspendCoroutine: ${e.message}")
                }
            }


            val okhttpJob = launch {
                try {
                    CoroutineApplication.initJob?.join()
                    val okhttp = suspendCancellableCoroutine {
                        //添加取消时的监听器
                        it.invokeOnCancellation {
                            println("okhttpJob cancelled")
                        }
                        mockGithub.contributorsCall("square", "okhttp")
                            .enqueue(object : Callback<List<Contributor>> {
                                override fun onResponse(
                                    call: Call<List<Contributor>>,
                                    response: Response<List<Contributor>>
                                ) {
                                    it.resume(response.body()!!)
                                }

                                override fun onFailure(
                                    call: Call<List<Contributor>>,
                                    throwable: Throwable
                                ) {
                                    it.resumeWithException(throwable)
                                }
                            })
                    }
                    println("suspendCancelableCoroutine: $okhttp")
                } catch (e: Exception) {
                    println("suspendCancelableCoroutine: ${e.message}")
                }
            }

            /**
             * 模拟请求时间为2s，1s后取消，
             * retrofitJob不会取消，因为suspendCoroutine不支持取消
             * okhttpJob会取消，因为suspendCancelableCoroutine支持取消
             */
            delay(1_000)
            retrofitJob.cancel()
            okhttpJob.cancel()
        }
    }

    /**
     * 回调式网络请求转换为挂起函数，返回网络请求的结果
     * suspendCoroutine 和 suspendCancellableCoroutine -
     * 相比于withContext，suspendCoroutine 和 suspendCancellableCoroutine 可以实现方便实现返回值为网络请求的结果
     */

    private suspend fun callbackToSuspend(): List<Contributor> {
        return suspendCoroutine {
            github.contributorsCall("square", "retrofit")
                .enqueue(object : Callback<List<Contributor>> {
                    override fun onResponse(
                        call: Call<List<Contributor>>,
                        response: Response<List<Contributor>>
                    ) {
                        it.resume(response.body()!!)
                    }

                    override fun onFailure(call: Call<List<Contributor>>, throwable: Throwable) {
                        it.resumeWithException(throwable)
                    }
                })
        }
    }

    /**
     * 尝试其他方法，好像都只是能拿到加入请求队列的函数的返回值，具体的请求结果是无法获取的
     */
    private suspend fun getCallbackResult() {
        //1、使用async启动协程？？？好像拿不到
        CoroutineScope(currentCoroutineContext()).async {
            CoroutineApplication.initJob?.join()
            mockGithub.contributorsCall("square", "okhttp")
                .enqueue(object : Callback<List<Contributor>> {
                    override fun onResponse(
                        call: Call<List<Contributor>>,
                        response: Response<List<Contributor>>
                    ) {

                    }

                    override fun onFailure(
                        call: Call<List<Contributor>>,
                        throwable: Throwable
                    ) {

                    }
                })
        }.await()

        //2、使用withContext？？？好像也拿不到
        withContext(Dispatchers.IO) {
            CoroutineApplication.initJob?.join()
            mockGithub.contributorsCall("square", "okhttp")
                .enqueue(object : Callback<List<Contributor>> {
                    override fun onResponse(
                        call: Call<List<Contributor>>,
                        response: Response<List<Contributor>>
                    ) {

                    }

                    override fun onFailure(
                        call: Call<List<Contributor>>,
                        throwable: Throwable
                    ) {

                    }
                })
        }
    }
}
