package com.example.fridaybarapp

import android.media.MediaDrm.LogMessage
import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
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

data class BarInfo(val name: String, val address: String, val page: String){
}

fun createListFromNetworkResponse(response: String): MutableList<BarInfo>{
    val returnList: MutableList<BarInfo> = mutableListOf()
    Log.v("test", "test")
    val trimEnd = response.substringBeforeLast("endOfBarList").substringAfter("startOfBarList");

    val nameString = trimEnd.substringAfter("name:").substringBefore("address:")
    val nameList = nameString.split(",_").dropLast(1)

    val addressString = trimEnd.substringAfter("address:").substringBefore("page:")
    val addressList = addressString.split(",_").dropLast(1)

    val pageString = trimEnd.substringAfter("page:")
    val pageList = pageString.split(",_").dropLast(1)

    Log.v("test", nameList.size.toString())
    if(nameList.isNotEmpty()) {
        for (n in nameList.indices) {
            returnList.add(BarInfo(nameList[n], addressList[n], pageList[n]))
        }
    }

    return returnList;
}