package com.example.aiyan.basiccoroutines._1_basics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.viewModelScope
import com.example.aiyan.basiccoroutines.github
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AndroidActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
        绑定当前组件的生命周期，生命周期结束时，取消所有的协程
        内置的ContinuationInterceptor == Dispatchers.Main.immediate 指定在主线程执行
        Dispatchers.Main.immediate：在主线程，直接执行协程代码
        Dispatchers.Main：使用handler.post将协程代码放到主线程执行
         */
        lifecycleScope
    }

    class AndroidViewModel : ViewModel() {
        /*
        在ViewModel中使用，和lifecycleScope使用的位置不同而已，都是Dispatchers.Main.immediate + SupervisorJob
         */
        private val vms = viewModelScope
    }

    /**
     * 客户端使用协程套路：
     * 开启运行在主线程的协程，使用挂起函数切换到后台线程执行耗时操作
     */

    private fun androidCoroutine() = CoroutineScope(Dispatchers.Main).launch {
        val contributors = github.contributors("square", "okhttp")
        println(contributors)
    }
}