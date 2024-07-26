package com.example.aiyan.basiccoroutines

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.aiyan.basiccoroutines.theme.BasicCoroutinesTheme
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException



class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        lifecycleScope.launch {
            try {
                callbackToSuspend()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private suspend fun suspendContributors() = github.contributors("square", "retrofit")

    private suspend fun callbackToSuspend() = suspendCancellableCoroutine {
        github.contributorsCall("square", "retrofit").enqueue(object : Callback<List<Contributor>> {
            override fun onResponse(
                call: Call<List<Contributor>>,
                response: Response<List<Contributor>>
            ) {
                it.resume(response.body()!!)
            }

            override fun onFailure(call: Call<List<Contributor>>, throwable: Throwable) {
                it.resumeWithException(throwable)
            }
        })
    }
}


@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    BasicCoroutinesTheme {
        Greeting("Android")
    }
}