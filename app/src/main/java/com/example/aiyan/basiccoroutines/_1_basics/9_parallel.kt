package com.example.aiyan.basiccoroutines._1_basics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.aiyan.basiccoroutines.github
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * 并行协程的交互
 *
 * 1、开启多个请求，并行执行，最后一起展示结果 - async
 * 2、单独需要一系列请求，之后的某个地方需要依赖这一系列请求完成（只需要知道完成）- join
 *
 * async启动协程：先并行，再调用await串行插入到其他协程中
 */
class ParallelActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /**
         * 依次开始网络请求，请求时间为所有网络请求时间之和
         */
        lifecycleScope.launch {
            val kotlinContributors = github.contributors("JetBrains", "Kotlin")
            val okhttpContributors = github.contributors("square", "okhttp")
            println(kotlinContributors + okhttpContributors)
        }

        /**
         * 同时开始两个耗时任务，并行执行，全部请求完成后显示结果
         * 请求时间为最长的网络请求时长
         */
        val deferred = lifecycleScope.async {
            github.contributors("JetBrains", "Kotlin")
        }
        lifecycleScope.launch {
            val okhttpContributors = github.contributors("square", "okhttp")
            val kotlinContributors = deferred.await()
            println(okhttpContributors + kotlinContributors)
        }

        /*
        简洁版本
         */
        lifecycleScope.launch {
            val kotlinDeferred = async {
                github.contributors("JetBrains", "Kotlin")
            }
            val okhttpDeferred = async {
                github.contributors("square", "okhttp")
            }
            println(kotlinDeferred.await() + okhttpDeferred.await())
        }

        /**
         * Java使用CompletableFuture实现（Java8推出的异步编程模型）
         */
        val future1 = github.contributorsCompletableFuture("JetBrains", "Kotlin")
        val future2 = github.contributorsCompletableFuture("square", "okhttp")
        future1.thenCombine(future2) { contributors1, contributors2 ->
            contributors1 + contributors2
        }.thenAccept {
            println(it)
        }

        /**
         * rxjava的实现
         */
        val observable1 = github.contributorsRxJava("JetBrains", "Kotlin")
        val observable2 = github.contributorsRxJava("square", "okhttp")
        val disposable = Observable.zip(
            observable1, observable2
        ) { t1, t2 -> t1 + t2 }.observeOn(AndroidSchedulers.mainThread()).subscribe {
            println(it)
        }

        /**
         * 后续的某个操作依赖之前的初始化完成
         */
        lifecycleScope.launch {

            val initJob = launch {
                delay(3000)
                println("init finished")
            }

            //执行一些不需要依赖初始化的操作
            delay(1_000)
            println("do something before init finished")

            //等待初始化完成
            initJob.join()

            //此时初始化已经完成
            println("do something after init finished")
        }
    }
}