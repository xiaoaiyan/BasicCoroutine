package com.example.aiyan.basiccoroutines.reflections

import android.os.Bundle
import androidx.activity.ComponentActivity

/**
 * NonCancellable：单例Job
 * SupervisorJob：子协程异常取消影响不到父协程
 */
class ScopeContextActivity: ComponentActivity(){
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }
}