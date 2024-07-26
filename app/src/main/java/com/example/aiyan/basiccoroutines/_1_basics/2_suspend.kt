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
            val contributors = github.contributors("square", "retrofit")
            showContributors(contributors)
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

            override fun onFailure(p0: Call<List<Contributor>>, p1: Throwable) {
                TODO("Not yet implemented")
            }
        })
    }

    private suspend fun callbackStyleToSuspend(): List<Contributor>{
        return suspendCancellableCoroutine {
            github.contributorsCall("square", "retrofit").enqueue(object : Callback<List<Contributor>> {
                override fun onResponse(
                    call: Call<List<Contributor>>,
                    response: Response<List<Contributor>>
                ) {
                    it.resumeWith(Result.success(response.body()!!))
                }

                override fun onFailure(call: Call<List<Contributor>>, throwable: Throwable) {
                    it.resumeWith(Result.failure(throwable))
                }
            })
        }
    }

    private fun showContributors(contributors: List<Contributor>) {
        for (contributor in contributors) {
            println(contributor)
        }
    }
}