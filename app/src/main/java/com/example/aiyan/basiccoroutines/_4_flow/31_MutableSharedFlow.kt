package com.example.aiyan.basiccoroutines._4_flow

import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.job
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

/**
 * Flow：数据流，从内部制定生产流程
 * MutableSharedFlow：事件流从外部发送数据（很正常）
 *
 * val mutableSharedFlow = MutableSharedFlow<Int>()
 * launch{
 *      while(ture) mutableSharedFlow.emit(1)
 * }
 * launch{
 *      mutableSharedFlow.collect{
 *
 *      }
 * }
 *
 * MutableSharedFlow和shareIn的选择：
 * 1、需要事件流实现，选择MutableSharedFlow
 * 2、已经有生产事件的Flow，选择shareIn（内部也是通过创建MutableSharedFlow实现的）
 *
 * 参数：
 * replay：缓冲 + 缓存尺寸（数据消费不及时时，上游最多缓冲的数据量 和 数据消费完毕后，继续保存下来给新订阅使用的数据量）
 * extraBufferCapacity：额外增加上游缓冲的数据
 * onBufferOverflow：缓冲溢出策略（只针对缓冲，上游数据发送太快时）
 *  注意：需要在调用collect之后才会生效（没有collect不存在上游数据发送太快下游来不及消费这回事，当然就无效了）
 *  没有collect时，缓存满后就是新数据覆盖就数据的策略
 *
 *  mutableSharedFlow.asSharedFlow：将MutableSharedFlow转换为ReadonlySharedFlow，暴露给外部订阅，但是不能发射数据时使用
 *
 *  TODO 在没有订阅之前，使用的是缓存大小（replay的值），缓存满后，新数据覆盖旧数据
 *  TODO 缓冲 = replay + extraBufferCapacity（存在缓冲策略）
 *  TODO 缓存 = replay（直接新数据覆盖旧数据）
 */

@OptIn(DelicateCoroutinesApi::class)
fun main() = runBlocking<Unit> {
    val stringSharedFlow = MutableSharedFlow<String>()
    launch(Dispatchers.Default) {
        stringSharedFlow.collect{
            println("collect: $it")
        }
    }
    launch {
        stringSharedFlow.emit("1")
        delay(1_000)
        stringSharedFlow.emit("2")
        delay(1_000)
        stringSharedFlow.emit("3")
    }

//    //缓冲为3，发送完毕后缓存为2
//    val mutableSharedFlow = MutableSharedFlow<Int>(2, 0, BufferOverflow.DROP_OLDEST)
//    GlobalScope.launch(coroutineContext.job) {
//        for (index in 1 .. 10){
////            delay(index * 100L) //100 1，200 2，600 3，1000 4，1500 5，2100 6，2800 7，3600 8，4500 9，5500 10
//            delay(500) //500 1，1000 2，1500 3，2000 4，2500 5，3000 6，3500 7，4000 8，4500 9，5000 10
//            mutableSharedFlow.emit(index)
//        }
//    }
//    launch(Dispatchers.Default) {
//        mutableSharedFlow.collect{
//            delay(1_000)
//            println("collect: $it")
//        }
//    }
//
//    GlobalScope.launch {
//        delay(2_000)
//        mutableSharedFlow.collect{
//            println("collect: $it")
//        }
//    }
}