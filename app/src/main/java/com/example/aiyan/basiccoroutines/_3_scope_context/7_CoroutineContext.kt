package com.example.aiyan.basiccoroutines._3_scope_context

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineName
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.EmptyCoroutineContext

/**
 * CoroutineContext的管理（+ - []）
 */

@OptIn(ExperimentalStdlibApi::class)
fun main() {
    // + 调用 plus 的operator函数（public operator fun plus(context: CoroutineContext): CoroutineContext）
    // 同类型的context直接相加（Job相加，CoroutineDispatcher相加），没意义，间接相加，后面替换前面的
    // CombineContext[CombineContext[Dispatchers.IO, Job()], CoroutineName("coroutine")]
    val coroutineContext = Dispatchers.IO + Job() + CoroutineName("coroutine")
    println(coroutineContext) // == Dispatchers.IO.plus(Job())

    //[] 调用 get 的operator函数（public operator fun <E : Element> get(key: Key<E>): E?）
    //Job：实际上是Job类的关联类companion object（Job.Key）
    coroutineContext[Job]
    coroutineContext[ContinuationInterceptor]
    coroutineContext[CoroutineDispatcher]
}