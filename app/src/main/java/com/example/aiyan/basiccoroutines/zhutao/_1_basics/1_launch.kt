package com.example.aiyan.basiccoroutines.zhutao._1_basics

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Text
import androidx.lifecycle.lifecycleScope
import com.example.aiyan.basiccoroutines.zhutao.github
import com.example.aiyan.basiccoroutines.zhutao.theme.BasicCoroutinesTheme
import kotlinx.coroutines.flow.asFlow
import kotlinx.coroutines.launch

/**
 * 查看当前协程：-Dkotlinx.coroutines.debug
 */

class LaunchCoroutinesActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            BasicCoroutinesTheme {
                Text(text = "Hello")
            }
        }

        lifecycleScope.launch {
            github.contributors("JetBrains", "Kotlin").asFlow().collect{
                println(it)
            }
        }
    }
}