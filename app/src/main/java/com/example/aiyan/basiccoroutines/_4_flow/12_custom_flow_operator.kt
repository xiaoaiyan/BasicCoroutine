package com.example.aiyan.basiccoroutines._4_flow

import com.example.aiyan.basiccoroutines.Contributor
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.FileInputStream

/**
 * 自定义操作符：（flow对象 生产 新的flow对象）
 * 1、Flow的扩展函数
 * 2、范型函数
 * 3、collect得到上游数据，emit发送数据到下游
 */

fun main() = runBlocking<Unit> {
    flowOf(1, 2, 3, 4, 5).noChange().collect {
        println("collect $it")
    }
    flowOf("okhttp", "okio", "retrofit").contributor().collect {
        println("collect $it")
    }
}

private fun <T> Flow<T>.noChange(): Flow<T> {
    return flow {
        collect{
            emit(it)
        }
    }
}

private fun Flow<String>.contributor() = channelFlow<List<Contributor>> {
    val gson = Gson()
    val rawType = object : TypeToken<List<Contributor>>() {}.rawType
    collect {
        launch(Dispatchers.IO) {
            val result = FileInputStream("square_$it.json").reader().use { reader ->
                gson.fromJson<List<Contributor>>(reader, rawType)
            }
            delay(2_000)
            send(result)
        }
    }
}

