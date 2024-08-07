package com.example.aiyan.basiccoroutines.reflections

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.aiyan.basiccoroutines.Contributor
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.Type
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import kotlin.coroutines.EmptyCoroutineContext
import kotlin.coroutines.resume

/**
 * 协程切线程：（启动协程的方式、挂起函数的方式）
 *
 * 1、coroutineScope.launch在指定的线程启动协程实现切线程
 * ————业务需要并行时，直接使用coroutineScope启动多个并行的协程
 * 2、在协程内部调用系统提供的切换线程的挂起函数（如：withContext）
 * ————业务需要串行时，启动一个协程，在协程中使用挂起函数切换线程
 *
 * TODO -------------------协程运行在哪里？？？-------------------
 * 1、launch时不指定Dispatchers，根据启动协程的coroutineScope中coroutineContext中的ContinuationInterceptor决定
 *      coroutineContext[ContinuationInterceptor] == null，运行在default线程池
 *      coroutineContext[ContinuationInterceptor] != null，运行在coroutineContext[ContinuationInterceptor]中
 * 2、launch时指定了Dispatchers，协程运行在指定的Dispatchers中
 *
 * async启动协程，规则和launch一致
 *
 * runBlocking启动协程，协程运行在runBlocking运行的线程或者指定线程/线程池运行
 *
 * TODO -------------------挂起函数都能切线程？？？-------------------
 * 1、launch、async、runBlocking都能切线程（启动协程，当然可以）
 * 2、withContext可以指定CoroutineScope，可以切线程
 *
 * TODO -------------------拿到耗时请求的返回结果？？？----------------
 * 1、suspendCancellableCoroutine：可以拿到回调型耗时请求的返回结果
 *  suspendCancellableCoroutine会一直挂起，直到调用resume
 * 2、async：发起耗时请求，在某个时刻需要使用返回结果，不支持回调型
 */

class BasicCoroutinesActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

//        whichThread()

//        whichFunctionCanChangeThread()

        lifecycleScope.launch(Dispatchers.IO){

            val result: String = suspendCancellableCoroutine {
                Executors.newSingleThreadScheduledExecutor().schedule(
                    {
                        it.resume("finished")
                    }, 3, TimeUnit.SECONDS
                )
            }
            println("$result ==== thread is ${Thread.currentThread().name}")
        }

        lifecycleScope.launch(Dispatchers.IO) {
            val deferred = async {
                delay(3_000)
                "finished"
            }
            deferred.await()
        }
    }

    /*
    ==================================协程运行在哪里？？？=======================================
     */
    private fun whichThread() {
        /**
         * 1、launch时不指定线程
         * 启动协程的CoroutineScope中ContinuationInterceptor为null时，协程运行在Default线程池
         * 启动协程的CoroutineScope中有指定ContinuationInterceptor时，运行在ContinuationInterceptor线程或者线程池中
         */
        CoroutineScope(EmptyCoroutineContext).launch {
            println("EmptyCoroutineContext == ${Thread.currentThread().name}")
        }
        CoroutineScope(CoroutineName("custom")).launch {
            println("CoroutineName Context == ${Thread.currentThread().name}")
        }
        CoroutineScope(Dispatchers.Main).launch {
            println("Dispatchers.Main Context == ${Thread.currentThread().name}")
        }

        /**
         * 2、在launch协程中指定Dispatchers，协程运行在指定的Dispatchers中
         */
        CoroutineScope(EmptyCoroutineContext).launch(Dispatchers.Main) {
            println("launch in Dispatchers.Main  == ${Thread.currentThread().name}")
        }

        CoroutineScope(Dispatchers.Main).launch(Dispatchers.IO) {
            println("launch in Dispatchers.IO == ${Thread.currentThread().name}")
        }
    }

    /*
    ==================================挂起函数都能切线程？？？=======================================
     */
    private fun whichFunctionCanChangeThread() {
        CoroutineScope(Dispatchers.Main).launch {
            println("launch ${Thread.currentThread().name}")

            //可以
            withContext(Dispatchers.IO) {
                println("withContext ${Thread.currentThread().name}")
            }

            //不可以
            coroutineScope {
                println("coroutineScope ${Thread.currentThread().name}")
            }

            //不可以
            suspendCancellableCoroutine {
                println("suspendCancellableCoroutine ${Thread.currentThread().name}")
            }
        }
    }

    /**
     * 回调 -> 挂起函数
     */
    private suspend fun callbackToSuspend() = suspendCancellableCoroutine {
        println("callbackToSuspend ${Thread.currentThread().name}")
        val okHttpClient = OkHttpClient.Builder().build()
        val request = Request.Builder()
            .url("https://api.github.com/repos/square/retrofit/contributors")
            .build()
        val call = okHttpClient.newCall(request)
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                TODO("Not yet implemented")
            }

            override fun onResponse(call: Call, response: Response) {
                println("onResponse ${Thread.currentThread().name}")
                val inputStream = response.body!!.byteStream()
                val result: List<Contributor> = Gson().fromJson(
                    inputStream,
                    object : TypeToken<List<Contributor>>() {}.type
                )
                it.resume(result)
            }
        })
    }

    private fun <T> Gson.fromJson(inputStream: InputStream, typeOfT: Type): T {
        return fromJson(inputStream.reader(), typeOfT)
    }
}

