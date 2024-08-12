package com.example.aiyan.basiccoroutines

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlin.coroutines.EmptyCoroutineContext

class CoroutineApplication : Application() {

    @OptIn(DelicateCoroutinesApi::class)
    override fun onCreate() {
        super.onCreate()
        initJob = GlobalScope.launch {
            initMockContributorWithFlow(this@CoroutineApplication)
        }
    }

    private suspend fun initMockContributorWithFlow(context: Context) {
        withContext(Dispatchers.IO) {
            val gson = Gson()
            val rawType = object : TypeToken<List<Contributor>>() {}.rawType
            flowOf("okhttp", "okio", "retrofit")
                .map { key ->
                    key to async {
                        context.assets.open("square_$key.json").reader().use {
                            gson.fromJson<List<Contributor>>(it, rawType)
                        }
                    }
                }.collect {
                    contributors[it.first] = it.second.await()
                }
        }
    }

    private suspend fun initMockContributor(context: Context) {
        if (contributors.isNotEmpty()) return
        contributors.putAll(withContext(Dispatchers.IO) {
            val gson = Gson()
            val rawType = object : TypeToken<List<Contributor>>() {}.type
            val okhttpDeferred = async {
                context.assets.open("square_okhttp.json").reader().use {
                    gson.fromJson<List<Contributor>>(it, rawType)
                }
            }
            val okioDeferred = async {
                context.assets.open("square_okio.json").reader().use {
                    gson.fromJson<List<Contributor>>(it, rawType)
                }
            }
            val retrofitDeferred = async {
                context.assets.open("square_retrofit.json").reader().use {
                    gson.fromJson<List<Contributor>>(it, rawType)
                }
            }
            mutableMapOf(
                "okhttp" to okhttpDeferred.await(),
                "okio" to okioDeferred.await(),
                "retrofit" to retrofitDeferred.await()
            )
        })
    }

    companion object {
        val contributors = mutableMapOf<String, List<Contributor>>()
        var initJob: Job? = null
    }
}