package com.example.aiyan.basiccoroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

//函数签名：
suspend fun getUserInfo1(){}

suspend fun getUserInfo2(): String{return ""}

suspend fun getUserInfo3(name: String): String{return ""}

suspend fun getUserInfo4(name: String) = withContext(Dispatchers.IO){""}