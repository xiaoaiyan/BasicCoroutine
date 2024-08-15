package com.example.aiyan.basiccoroutines.reflections

import android.os.Bundle
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.lifecycleScope
import com.example.aiyan.basiccoroutines.R
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.channelFlow
import kotlinx.coroutines.flow.consumeAsFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlin.time.Duration.Companion.seconds

/**
 * 过滤类型：10、11、12、13、14
 *
 */

class FlowActivity : ComponentActivity() {

    @OptIn(FlowPreview::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flow)
        val editText = findViewById<EditText>(R.id.edit_text)
        lifecycleScope.launch {
//            val channel = Channel<String>()
//            editText.addTextChangedListener(afterTextChanged = {
//                runBlocking {
//                    channel.send(it?.toString()?: "")
//                }
//            })
//            channel.consumeAsFlow().debounce(5.seconds).collect{
//                println("collect $it")
//            }
        }


        lifecycleScope.launch {
            callbackFlow {
                editText.addTextChangedListener(afterTextChanged = {
                    trySend(it?.toString() ?: "")
                })
                awaitClose()
            }.debounce(1.seconds).collect {
                println("collect $it")
            }
        }
    }
}