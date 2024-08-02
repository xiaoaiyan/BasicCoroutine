package com.example.aiyan.basiccoroutines._1_basics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.aiyan.basiccoroutines.github
import kotlinx.coroutines.launch

/**
 * 为什么挂起函数不卡线程？？？？？？？？ ---- 底层实现 回调
 */
class DeMagicActivity: ComponentActivity(){

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        getContributors()
    }

    private fun getContributors(){
        lifecycleScope.launch {
            val contributorList = github.contributors("square", "okhttp")
            println(contributorList)
        }
    }

    /**
     * 1、直接向线程中插入代码执行？？？？做不到
     *
     * 2、Android切换到主线程执行的机制：Handler机制
     * 主线程循环执行message，子线程发送message到主线程，代码就到主线程执行了 ---- 也不是直接插入代码执行
     *
     * 3、插入代码？？？？谁都做不到
     */

    /**
     * 那协程呢？怎么做到切出去再切回来？？？？
     *
     * 启动协程：
     * 将 block: suspend CoroutineScope.() -> Unit 函数类型对象，交给ContinuationInterceptor去执行
     *
     * Dispatchers.Default：将block交给自己的线程池去执行
     * Dispatchers.Main：通过handler将block传递到主线程
     *
     * 挂起函数：
     * kotlin在编译过程中实现，在挂起函数前后分界，将协程切割成一个个的回调实现（状态机）
     *
     * 切后台：block交给线程池实现
     * 切前台：block交给handler向主线程post一个message
     */
}