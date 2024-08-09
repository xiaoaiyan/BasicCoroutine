package com.example.aiyan.basiccoroutines._3_scope_context

/**
 * CoroutineContext：协程上下文，包含协程的所有信息
 *  Job：结构化流程相关信息
 *  ContinuationInterceptor：执行线程相关信息
 *  。。。
 *
 * CoroutineScope：（定位）
 * 1、是CoroutineContext的容器，存储当前协程代码块的上下文信息（非手动创建的）
 * 2、启动协程，创建协程需要上下文信息，CoroutineScope启动协程能直接得到上下文信息
 *
 * 手动自己创建的coroutineScope，不对应任何协程
 */

fun main() {

}