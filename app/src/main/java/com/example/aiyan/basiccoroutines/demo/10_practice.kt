package com.example.aiyan.basiccoroutines.demo

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.Call
import okhttp3.Callback
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import okhttp3.ResponseBody
import okhttp3.internal.threadFactory
import okhttp3.logging.HttpLoggingInterceptor
import okio.buffer
import okio.sink
import okio.source
import retrofit2.Retrofit
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.concurrent.SynchronousQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * 1m后进程才会结束（所有的非守护线程结束，进程才会结束）
 */
private fun processOver() {
    val threadPoolExecutor = ThreadPoolExecutor(
        0, Int.MAX_VALUE, 60, TimeUnit.SECONDS,
        SynchronousQueue(), threadFactory("Okhttp Dispatcher", false)
    )
    threadPoolExecutor.execute {
        println("this is in thread pool")
    }
}

fun main() {
//    processOver()

    downloadImage()
}

private const val IMAGE_URL =
    "http://gips2.baidu.com/it/u=195724436,3554684702&fm=3028&app=3028&f=JPEG"

private fun downloadImage() {
    runBlocking {
        writeImage(downloadWithOkhttpSuspendCancellableCoroutine())
    }
}

interface DownloadImage {
    @GET("/it/u={u}&fm={fm}&app={app}&f={f}")
    fun download(
        @Path("u") u: String,
        @Path("fm") fm: String,
        @Path("app") app: String,
        @Path("f") f: String
    ): retrofit2.Call<ResponseBody>
}

private val retrofitClient = OkHttpClient.Builder().addInterceptor(
    HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BASIC)
).build()
private val downloadImage =
    Retrofit.Builder().client(retrofitClient).baseUrl("https://gips3.baidu.com").build()
        .create<DownloadImage>()

private suspend fun downloadWithRetrofitSuspendCancellableCoroutine(): ByteArray {
    return suspendCancellableCoroutine {
        val responseBodyCall = downloadImage.download(
            "195724436,3554684702",
            "3028",
            "3028",
            "JPEG"
        )
        responseBodyCall.enqueue(object : retrofit2.Callback<ResponseBody> {
            override fun onResponse(
                call: retrofit2.Call<ResponseBody>,
                response: retrofit2.Response<ResponseBody>
            ) {
                it.resume(response.body()!!.bytes())
                //线程池存活线程1分钟，会阻碍进程结束
//                retrofitClient.dispatcher.executorService.shutdown()
            }

            override fun onFailure(call: retrofit2.Call<ResponseBody>, throwable: Throwable) {
                it.resumeWithException(throwable)
            }
        })
    }
}


private val okHttpClient = OkHttpClient.Builder().build()
private val request = Request.Builder()
    .url("https://gips2.baidu.com/it/u=195724436,3554684702&fm=3028&app=3028&f=JPEG").build()
private val call = okHttpClient.newCall(request)

private suspend fun downloadWithOkhttpSuspendCancellableCoroutine(): ByteArray {
    return suspendCancellableCoroutine {
        call.enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                it.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                it.resume(response.body!!.bytes())
//                okHttpClient.dispatcher.executorService.shutdown()
            }
        })
    }
}

private suspend fun handleImage(byteArray: ByteArray) = withContext(Dispatchers.Default) {
    val bitmap = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    val grayBitmap = grayBitmap(bitmap)
    val byteArrayOutputStream = ByteArrayOutputStream()
    grayBitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream)
    byteArrayOutputStream.toByteArray()
}

private fun grayBitmap(bitmap: Bitmap): Bitmap {

    //实现一
//    val grayBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.ARGB_8888)
//    val canvas = Canvas(grayBitmap)
//    val colorMatrix = ColorMatrix()
//    colorMatrix.setSaturation(0f)
//    val paint = Paint()
//    paint.colorFilter = ColorMatrixColorFilter(colorMatrix)
//    canvas.drawBitmap(bitmap, 0f, 0f, paint)

    //实现二
    val grayBitmap = Bitmap.createBitmap(bitmap.width, bitmap.height, Bitmap.Config.RGB_565)
    for (x in 0 until bitmap.width) {
        for (y in 0 until bitmap.height) {
            val pixel = bitmap.getPixel(x, y)
            val red = pixel shr 16 and 0xff
            val green = pixel shr 8 and 0xff
            val blue = pixel and 0xff
            val gray = (red * 0.3 + green * 0.59 + blue * 0.11).toInt()
            grayBitmap.setPixel(x, y, gray shl 16 or (gray shl 8) or gray)
        }
    }
    return grayBitmap
}

private suspend fun writeImage(
    byteArray: ByteArray,
    fileName: String = "./app/src/main/java/com/example/aiyan/basiccoroutines/image.jpeg"
) = withContext(Dispatchers.IO) {
    File(fileName).sink().buffer().use {
        it.writeAll(byteArray.inputStream().source())
        it.flush()
    }
}

class PracticeCoroutineActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent { }
        lifecycleScope.launch {
            val file = File(getExternalFilesDir(null), "image.jpeg")
            writeImage(
                handleImage(downloadWithRetrofitSuspendCancellableCoroutine()),
                fileName = file.absolutePath
            )
        }
    }
}