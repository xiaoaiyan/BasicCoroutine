package com.example.aiyan.basiccoroutines._1_basics

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.aiyan.basiccoroutines.Contributor
import com.example.aiyan.basiccoroutines.github
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SuspendFunctionActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        coroutineStyle()

        callbackStyle()
    }

    private fun coroutineStyle() {
        CoroutineScope(Dispatchers.Main).launch {
            /**
             * 挂起函数执行完后自动切回来了
             *
             * 挂起：就是稍后会切回来的一个线程切换执行的操作（本质上是回调实现）
             *
             * 最终需要直接或者间接调用挂起函数（协程框架自带的实现了挂起的挂起函数）
             */
            val contributors = github.contributors("square", "retrofit") //后台线程执行
            showContributors(contributors) //主线程执行
            /**
             * suspend挂起：协程让出正在执行它的线程 直到 挂起函数执行完成（挂起函数会在它指定的线程执行完成）
             * ——————————github.contributors("square", "retrofit")
             * 恢复：挂起函数执行完成后，继续执行挂起函数后面的代码
             * ——————————showContributors(contributors)
             *
             * suspend挂起函数，协程与线程脱离，故挂起函数只能在协程中/挂起函数中执行
             */
        }
    }


    private fun callbackStyle() {
        github.contributorsCall("square", "retrofit").enqueue(object : Callback<List<Contributor>> {
            override fun onResponse(
                call: Call<List<Contributor>>,
                response: Response<List<Contributor>>
            ) {
                showContributors(response.body()!!)
            }

            override fun onFailure(call: Call<List<Contributor>>, th: Throwable) {
            }
        })
    }

    private fun showContributors(contributors: List<Contributor>) {
        for (contributor in contributors) {
            println(contributor)
        }
    }
}