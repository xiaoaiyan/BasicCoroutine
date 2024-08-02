package com.example.aiyan.basiccoroutines.demo

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.aiyan.basiccoroutines.unstableGithub
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import java.util.concurrent.TimeoutException

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
            flow.collect {
                try {
                    val contributors = unstableGithub.contributors("square", "okhttp")
                    println(contributors)
                } catch (timeoutException: TimeoutException) {
                    println(timeoutException.message)
                }
            }
        }
    }
}