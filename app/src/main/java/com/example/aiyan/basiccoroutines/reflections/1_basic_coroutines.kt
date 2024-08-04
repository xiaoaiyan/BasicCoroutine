package com.example.aiyan.basiccoroutines.reflections

/**
 * 协程切线程：（启动协程的方式、挂起函数的方式）
 *
 * 1、coroutineScope.launch在指定的线程启动协程实现切线程
 * ————业务需要并行时，直接使用coroutineScope启动多个并行的协程
 * 2、在协程内部调用系统提供的切换线程的挂起函数（如：withContext）
 * ————业务需要串行时，启动一个协程，在协程中使用挂起函数切换线程
 */

fun main() {

}
