package com.example.aiyan.basiccoroutines.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.aiyan.basiccoroutines.CoroutineApplication
import com.example.aiyan.basiccoroutines.unstableGithub
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch

class FlowTryCatchActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val flow = flow {
            emit(1)
            emit(2)
            emit(3)
            emit(4)
            emit(5)
        }

        lifecycleScope.launch {
            CoroutineApplication.initMockContributor(this@FlowTryCatchActivity)

            try {
                flow.collect {
                    val contributors = unstableGithub.contributors("square", "okhttp")
                    println(contributors)
                }
            } catch (e: Exception) {
                println("catch in launch ${e.message}")
            }
        }
    }
}