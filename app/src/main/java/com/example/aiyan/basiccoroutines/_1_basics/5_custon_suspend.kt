package com.example.aiyan.basiccoroutines._1_basics

import com.example.aiyan.basiccoroutines.Contributor
import com.example.aiyan.basiccoroutines.github
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * 普通函数添加 suspend 关键字，表示该函数是一个挂起函数
 *
 * 正确的思维：业务函数，里面需要调用别的挂起函数（具体业务功能的挂起函数），给函数添加suspend关键字，可以调用别的挂起函数
 *
 * 特殊的挂起函数 withContext：使用withContext指明运行线程，将业务功能放置在指定的线程执行。
 * 在抽取业务功能时需要将withContext一起，标记该函数就是个耗时函数，只能在协程调用
 *
 * suspend：相当于开关，有suspend关键字的函数才能调用其他挂起函数
 *
 * 性能损耗：在错误的线程执行性能损耗的业务代码
 * 挂起函数在语法层面提供的性能优势：在函数创建时，创建者通过withContext指定在特定的线程池运行
 * 调用者在调用时不用关心函数是否耗时
 *
 * TODO 正确的线程执行正确的操作，避免性能损耗
 */

//调用其他业务挂起函数
private suspend fun getRetrofitContributors(): List<Contributor> {
    return github.contributors("square", "retrofit")
}

//调用withContext
private suspend fun getData(): String =
    withContext(Dispatchers.IO) {
        Thread.sleep(3000)
        "success"
    }

/*
redundant：多余的，冗余的
get函数的suspend，白白增加限制，只能在协程或者其他挂起函数中调用

redundant suspend modifier
 */
private suspend fun get() {

}

