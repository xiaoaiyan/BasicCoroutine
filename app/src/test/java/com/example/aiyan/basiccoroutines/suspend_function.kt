package com.example.aiyan.basiccoroutines

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

suspend fun getData1(){}

suspend fun getData2(name: String): String {
    return ""
}

suspend fun getData3(name: String): String = withContext(Dispatchers.IO) { "" }