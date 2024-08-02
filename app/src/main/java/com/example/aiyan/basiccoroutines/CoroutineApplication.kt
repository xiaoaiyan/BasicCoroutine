package com.example.aiyan.basiccoroutines

import android.app.Application
import android.content.Context
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class CoroutineApplication : Application() {

    override fun onCreate() {
        super.onCreate()
        runBlocking {
            initMockContributorWithFlow(this@CoroutineApplication)
        }
    }

    private suspend fun initMockContributorWithFlow(context: Context) {
        if (contributors.isNotEmpty()) return
        val gson = Gson()
        val rawType = object : TypeToken<List<Contributor>>() {}.type
        withContext(Dispatchers.IO){
            flow {
                emit("okhttp")
                emit("okio")
                emit("retrofit")
            }.map {key ->
                key to async {
                    context.assets.open("square_$key.json").reader().use {
                        gson.fromJson<List<Contributor>>(it, rawType)
                    }
                }
            }.collect{
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
    }
}