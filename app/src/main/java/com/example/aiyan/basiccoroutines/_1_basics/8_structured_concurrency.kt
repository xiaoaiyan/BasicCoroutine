package com.example.aiyan.basiccoroutines._1_basics

import android.os.Bundle
import androidx.activity.ComponentActivity
import com.example.aiyan.basiccoroutines.Contributor
import com.example.aiyan.basiccoroutines.github
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.Disposable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.EmptyCoroutineContext

/**
 * 挂起函数：简化并发代码的结构，简化并发任务的写法
 * 结构化并发：并发任务的管理
 *
 * GCRoots：static对象、正在运行的线程、来自JNI对象
 *
 * 被GCRoots对象直接或者间接引用的所有对象都是不可回收的
 */
class StructuredConcurrencyActivity : ComponentActivity() {

    private var disposable: Disposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        /*
        界面关闭，但是网络请求还在继续，网络请求完成后会执行界面刷新的代码
        此时会出现，活跃线程间接持有activity的引用，导致无法及时回收，内存泄漏


        可是，网络请求完成，后台线程结束，内存泄漏也就结束了，可以被GC回收
         */


        //协程模式
        val job = CoroutineScope(Dispatchers.Main).launch {
            val contributors = github.contributors("square", "retrofit")
            println("======suspend, thread is = ${Thread.currentThread().name}")
            println(contributors)
        }
        //启动协程会返回Job类型的对象，调用job.cancel()可以随时取消协程
        job.cancel() // 取消协程

        //callback模式
        github.contributorsCall("square", "retrofit").enqueue(object : Callback<List<Contributor>> {
            override fun onResponse(
                call: Call<List<Contributor>>,
                response: Response<List<Contributor>>
            ) {
                println("======call, thread is = ${Thread.currentThread().name}")
                println(response.body()!!)
            }

            override fun onFailure(call: Call<List<Contributor>>, throwable: Throwable) {
            }
        })

        //rxjava模式
        disposable = github.contributorsRxJava("square", "retrofit")
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                println("======rxjava, thread is = ${Thread.currentThread().name}")
                println(it)
            }


        /**
         * 结构化并发：协程存在一层层的结构化的父子关系
         * 这些协程的相互配合和取消或者异常的管理
         *
         * coroutineScope管理parent，parent管理child。。。。。。
         */
        val coroutineScope = CoroutineScope(EmptyCoroutineContext)
        val parent = coroutineScope.launch {
            val child = launch {

            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        disposable?.dispose()
    }
}

/**
 * 页面关闭后，后台依然还在执行没用的任务的解决：
 *
 * 1、从内存泄漏角度解决，通过弱引用，GC回收时释放资源
 *
 * 2、RxJava解决：onDestroy中调用disposable.dispose取消rxjava的后续流程，防止内存泄漏
 * 直接掐断业务链条，结束后续流程
 *
 * 3、协程解决：启动协程时返回的Job对象，调用job.cancel取消协程，或者，
 * 调用coroutineScope.cancel()取消由coroutineScope启动的所有协程树
 *
 * 结构化并发：
 * coroutineScope.cancel() - 取消所有协程
 *
 * 协程代码块中的隐式的coroutineScope，和协程是一一对应的关系，就可以看成是启动的协程
 * 每个协程{}中都由一个隐式的coroutineScope，this
 */