package com.example.aiyan.basiccoroutines

import io.reactivex.rxjava3.core.Observable
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.OkHttpClient
import retrofit2.Call
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.create
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.mock.BehaviorDelegate
import retrofit2.mock.MockRetrofit
import retrofit2.mock.NetworkBehavior
import java.util.concurrent.CompletableFuture
import java.util.concurrent.TimeoutException
import kotlin.coroutines.EmptyCoroutineContext

private const val BASE_URL = "https://api.github.com"
private val okHttpClient = OkHttpClient.Builder().build()
private val retrofit =
    Retrofit.Builder().client(okHttpClient).baseUrl(BASE_URL).addCallAdapterFactory(RxJava3CallAdapterFactory.create())
        .addConverterFactory(GsonConverterFactory.create()).build()
val github = retrofit.create<Github>()

fun cancelRetrofit(){
    okHttpClient.dispatcher.executorService.shutdown()
}

private val mockBehavior = NetworkBehavior.create().apply {
    //默认模拟的请求时间2s
//    setFailurePercent(40)
//    setFailureException(TimeoutException("Connection time out!"))
}
private val mockRetrofit = MockRetrofit.Builder(retrofit).networkBehavior(mockBehavior).build()
private val mockDelegate = mockRetrofit.create(Github::class.java)
val mockGithub: Github = MockGithub(mockDelegate)

interface Github {
    //"https://api.github.com/repos/{owner}/{repo}/contributors",
    @GET("/repos/{owner}/{repo}/contributors")
    fun contributorsCall(
        @Path("owner") owner: String, //square
        @Path("repo") repo: String //retrofit
    ): Call<List<Contributor>>

    @GET("/repos/{owner}/{repo}/contributors")
    suspend fun contributors(
        @Path("owner") owner: String, //square
        @Path("repo") repo: String //retrofit
    ): List<Contributor>

    @GET("/repos/{owner}/{repo}/contributors")
    fun contributorsRxJava(
        @Path("owner") owner: String, //square
        @Path("repo") repo: String //retrofit
    ): Observable<List<Contributor>>

    @GET("/repos/{owner}/{repo}/contributors")
    fun contributorsCompletableFuture(
        @Path("owner") owner: String, //square
        @Path("repo") repo: String //retrofit
    ): CompletableFuture<List<Contributor>>
}

private class MockGithub(private val delegate: BehaviorDelegate<Github>) : Github {

    private fun ensureNotEmpty(): Map<String, List<Contributor>>{
//        return runBlocking {
//            if (CoroutineApplication.initJob?.isCompleted == false){
//                CoroutineApplication.initJob?.join()
//            }
//            CoroutineApplication.contributors
//        }
        return CoroutineApplication.contributors
    }

    override fun contributorsCall(owner: String, repo: String): Call<List<Contributor>> {
        val contributors = ensureNotEmpty()[repo]
        return delegate.returningResponse(contributors).contributorsCall(owner, repo)
    }

    override suspend fun contributors(owner: String, repo: String): List<Contributor> {
        val contributors = ensureNotEmpty()[repo]
        return delegate.returningResponse(contributors).contributors(owner, repo)
    }

    override fun contributorsRxJava(owner: String, repo: String): Observable<List<Contributor>> {
        val contributors = ensureNotEmpty()[repo]
        return delegate.returningResponse(contributors).contributorsRxJava(owner, repo)
    }

    override fun contributorsCompletableFuture(owner: String, repo: String): CompletableFuture<List<Contributor>> {
        val contributors = ensureNotEmpty()[repo]
        return delegate.returningResponse(contributors).contributorsCompletableFuture(owner, repo)
    }
}


data class Contributor(
    val avatar_url: String,
    val contributions: Int,
    val events_url: String,
    val followers_url: String,
    val following_url: String,
    val gists_url: String,
    val gravatar_id: String,
    val html_url: String,
    val id: Int,
    val login: String,
    val node_id: String,
    val organizations_url: String,
    val received_events_url: String,
    val repos_url: String,
    val site_admin: Boolean,
    val starred_url: String,
    val subscriptions_url: String,
    val type: String,
    val url: String
)