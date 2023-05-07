package com.barbuddy.fridaybarapp

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import java.io.IOException

suspend fun makeNetworkRequest(): String? {
    return withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://app.dokkedalleth.dk")
            .build()

        try {
            val response = client.newCall(request).execute()
            return@withContext response.body?.string()
        } catch (e: IOException) {
            e.printStackTrace()
            val list = listOf<String>("error")
            return@withContext list.toString()
        }
    }
}

class BarInfo(_name: String, _address: String, _page: String){
    var name: String = _name;
    var address: String = _address;
    var page: String = _page;
}

fun createListFromNetworkResponse(response: String): String {
    var trimEnd = response.substringBeforeLast("endOfBarList").substringAfter("startOfBarList");
    return trimEnd
}

suspend fun makeNetworkRequestJSON(): JSONArray? {
    return withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("https://app.dokkedalleth.dk/bars.json")
            .build()

        try {

            val response = client.newCall(request).execute()
            val jsonString = response.body?.string()
            return@withContext jsonString?.let { JSONArray(it) }
        } catch (e: IOException) {
            e.printStackTrace()
            return@withContext null
        }
    }
}