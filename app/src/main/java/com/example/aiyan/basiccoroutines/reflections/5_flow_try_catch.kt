package com.example.aiyan.basiccoroutines.reflections

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.example.aiyan.basiccoroutines.CoroutineApplication
import com.example.aiyan.basiccoroutines.mockGithub
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
            CoroutineApplication.initJob?.join()
            flow.collect {
                try {
                    val contributors = mockGithub.contributors("square", "okhttp")
                    println(contributors)
                } catch (timeoutException: TimeoutException) {
                    println(timeoutException.message)
                }
            }
        }
    }
}