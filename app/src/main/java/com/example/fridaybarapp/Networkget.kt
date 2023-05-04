package com.example.fridaybarapp

import okhttp3.OkHttpClient
import okhttp3.Request
import java.io.IOException

fun makeNetworkRequest(): String? {
    val client = OkHttpClient()
    val request = Request.Builder()
        //.url("https://maps.googleapis.com/maps/api/geocode/json?key=AIzaSyCcOgjIpA0cnS6OFZJZdHxcZOhjcArLTow&place_id=ChIJg6LFyQE_TEYRueo5XCvJ8ck")
        .url("https://app.dokkedalleth.dk")
        .build()

    try {
        val response = client.newCall(request).execute()
        return response.body?.string()
    } catch (e: IOException) {
        e.printStackTrace()
        return "didnt work"
    }
}

class BarInfo(_name: String, _address: String, _page: String){
    var name: String = _name;
    var address: String = _address;
    var page: String = _page;
}

fun createListFromNetworkResponse(response: String): List<BarInfo>{
    var trimEnd = response.substringBeforeLast("endOfBarList").substringAfter("startOfBarList");

}