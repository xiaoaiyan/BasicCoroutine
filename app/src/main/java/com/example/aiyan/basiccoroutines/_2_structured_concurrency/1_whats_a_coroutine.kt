package com.example.aiyan.basiccoroutines._2_structured_concurrency

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.coroutines.ContinuationInterceptor
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 结构化并发：父子协程生命周期关系的管理
 * 1、结构化结束
 * 2、结构化取消
 * 3、结构化异常
 *
 * 什么是一个协程？？？
 *     Job？ - 专注协程流程相关
 *     CoroutineScope？ - 协程顶层管理类
 *     block代码块？ - 协程具体的执行代码
 *
 * 一个协程：
 * launch和async启动协程，返回协程对象的Job，或者协程代码块中的隐式的CoroutineScope「this」都可以看成是「一个协程」
 * 事实上，就是同一个对象
 *
 * 协程抽象类：AbstractCoroutine：既是Job，也是CoroutineScope，也是Continuation
 */

fun main() = runBlocking<Unit>{
    /**
     *
     * Job能干什么？？？
     * 1、管理启动，取消
     * 2、获得协程状态
     *
     * 只关注流程时，可以将Job对象看成是协程对象（只是包含协程流程相关的功能）
     */

    /**
     * CoroutineScope：协程顶层管理类，包含协程所有的上下文信息，所有的功能和属性都能找到
     *
     * 可以看成一个协程
     */
    val scope = CoroutineScope(EmptyCoroutineContext)
    val launchJob = scope.launch {
        println("this: $this")
        val job = coroutineContext[Job]
        println("job: $job")
        val continuationInterceptor = coroutineContext[ContinuationInterceptor]
        println("continuationInterceptor: $continuationInterceptor")
    }
    println("launchJob: $launchJob")
}

/**
 * launch启动返回协程对象Job，为什么不是StandaloneCoroutine或者LazyStandaloneCoroutine呢？？？
 *  --责任拆分，开发者只能使用Job类中的API
 * async返回的Deferred对象，继承自Job，能够通过await得到协程的返回结果
 *
 * Job对象的功能：
 *
 * job.start() //启动模式为CoroutineStart.LAZY，调用start方法才会启动协程
 *
 * job.cancel() //取消协程
 *
 * job.join() //挂起函数，插入其他协程内部执行
 *
 * job.isActive //运行
 * job.isCancelled //取消
 * job.isCompleted //结束（不管什么方式结束的）
 *
 * job.children //子协程
 * job.parent //父协程
 */