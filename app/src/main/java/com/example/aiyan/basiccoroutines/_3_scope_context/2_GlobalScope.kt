package com.example.aiyan.basiccoroutines._3_scope_context

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlin.coroutines.EmptyCoroutineContext

/**
 * DelicateCoroutinesApi：提醒当前API容易用错，小心使用
 *
 * GlobalScope：单例，没有Job
 *
 * 1、不管是自己创建的CoroutineScope或者是启动协程（launch、async），系统创建的CoroutineScope，它的
 * coroutineContext中都会存在Job
 *
 * 获得Job，唯一可能为null的，GlobalScope.coroutineContext[Job]
 *
 * 2、由于GlobalScope没有Job，所有由其启动的协程，是没有父Job的
 *
 * 使用：需要启动不绑定生命周期的协程，或者是生命周期是整个应用
 *
 * GlobalScope创建的协程是需要没有关联的，不能某个协程链异常影响到其他的协程链取消
 * 如何做到：GlobalScope中没有Job，让它启动的协程都是没有父Job的
 *
 * 启动的协程没有和任何父Job绑定，所以不会被任何组件的关闭而自动取消协程，会有资源浪费的风险而已
 * 和这样
 * CoroutineScope(EmptyCoroutineContext).launch {
 *         println("EmptyCoroutineContext parent job: ${coroutineContext[Job]!!.parent}")
 *     }
 * 启动协程，只要不手动结束协程，和GlobalScope一样
 *
 * 重点：如何管理好协程，而不是不实用GlobalScope
 */
@OptIn(DelicateCoroutinesApi::class)
fun main() {
    CoroutineScope(EmptyCoroutineContext).launch {
        println("EmptyCoroutineContext parent job: ${coroutineContext[Job]!!.parent}")
    }


    //依然可以拿到启动的协程，手动管理
    val launch = GlobalScope.launch {
        println("GlobalScope parent job: ${coroutineContext[Job]!!.parent}")
    }

    Thread.sleep(3_000)
}