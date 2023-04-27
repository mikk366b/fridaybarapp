package com.example.fridaybarapp

import android.os.Bundle
import android.util.JsonReader
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.runtime.*
import com.example.fridaybarapp.ui.theme.FridaybarappTheme
import io.ktor.client.*
import io.ktor.client.engine.okhttp.*
import io.ktor.client.request.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.fridaybarapp.ui.theme.FridaybarappTheme
import io.ktor.http.cio.*
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import kotlin.math.log


class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FridaybarappTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colors.background) {
                    NetworkResponseUI()
                }
            }
        }
    }
}

@Composable
fun Greeting(name: String) {
    Text(text = "Hello $name! Hej Igen")
}


@Composable
fun DefaultPreview() {
    FridaybarappTheme {
        Greeting("Android")
    }
}
fun makeNetworkRequesttest(): String? {
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




@Composable
fun NetworkResponseUI() {
    var response by remember { mutableStateOf("") }

    // Perform the network operation in a coroutine
    LaunchedEffect(true) {
        val result = withContext(Dispatchers.IO) {
            makeNetworkRequesttest()
        }

        // Update the state variable with the response
        if (result != null) {
            response = result as String
        }
    }

    // Display the response in a Text composable
    Column {
        Text(
            text = "Network Response:"
        )
        //Text(text = response)
        Log.v("JSON response før",response)
        Text(text = response)




    }
}