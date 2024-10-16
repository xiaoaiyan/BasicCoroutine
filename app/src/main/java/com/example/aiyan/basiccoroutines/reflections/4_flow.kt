package com.example.aiyan.basiccoroutines.reflections

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText
import androidx.activity.ComponentActivity
import androidx.core.widget.addTextChangedListener
import androidx.core.widget.doAfterTextChanged
import androidx.lifecycle.lifecycleScope
import com.example.aiyan.basiccoroutines.R
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.time.Duration.Companion.seconds

/**
 * 过滤类型：10、11、12、13、14
 *
 * SharedFlow
 */

class FlowActivity : ComponentActivity() {

    @OptIn(FlowPreview::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flow)
        val editText = findViewById<EditText>(R.id.edit_text)
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

