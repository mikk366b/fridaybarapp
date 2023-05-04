package com.example.fridaybarapp

import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.runtime.*
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.example.fridaybarapp.firestore.service.FireStore
import com.example.fridaybarapp.ui.theme.FridaybarappTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import com.example.fridaybarapp.ui.theme.FridaybarappTheme
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.MarkerOptions
import io.ktor.http.cio.*
import kotlinx.coroutines.withContext
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException

import kotlin.math.log
import com.google.android.libraries.maps.CameraUpdate
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.model.PolylineOptions
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.*

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            FridaybarappTheme {
                // A surface container using the 'background' color from the theme
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF014C2D)) {
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
fun Bars(response: String) {
    var viewDetails by remember { mutableStateOf(false) }
    if(!viewDetails) {
        Image(painter = painterResource(id = R.drawable.krone),
            modifier = Modifier
                .height(80.dp)
                .fillMaxWidth(),
            contentDescription = null,
            alignment = Alignment.Center
        )
        Row(
            Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(Color(0xFFCAA800))){
        }
        Row(
            Modifier
                .height(35.dp)
                .fillMaxWidth()
                .background(Color(0xFFB90000))) {
            Text(text = "List of friday bars", Modifier.offset(x = 10.dp, y = -(2).dp),
                style = TextStyle(color = Color(0xFFFFF0D2),
                    fontSize = 25.sp,
                    fontWeight = FontWeight.SemiBold,
                    shadow = Shadow(color = Color(0xFF000000), offset = Offset(x = 2f, y = 2f), blurRadius = 1f)
                ))
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(Color(0xFFCAA800))){
        }

        //Text(text = response)
        Log.v("JSON response før", response)
        val parts = response.lines()
        for (i in 0..8) {
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                Modifier
                    .height(80.dp)
                    .width(390.dp)
                    .offset(x = 10.dp)
                    .clickable { viewDetails = !viewDetails },
                shape = RoundedCornerShape(20),
                backgroundColor = Color(0xFF000000),
                border = BorderStroke(2.dp, color = Color(0xFFA36D00))) {
                Column {
                    Text(
                        text = "Approksimerbar",
                        Modifier.offset(x = 10.dp, y = 2.dp),
                        style = TextStyle(
                            color = Color(0xFFFFF0D2),
                            fontSize = 25.sp,
                            fontWeight = FontWeight.SemiBold,
                            shadow = Shadow(
                                color = Color(0xFFE70000),
                                offset = Offset(x = 5f, y = 4f),
                                blurRadius = 2f
                            )
                        )
                    )
                    Text(
                        text = "Adresse",
                        Modifier.offset(x = 10.dp, y = 2.dp),
                        style = TextStyle(
                            color = Color(0xFFFFF0D2),
                            fontSize = 25.sp,
                            fontWeight = FontWeight.SemiBold,
                            shadow = Shadow(
                                color = Color(0xFFE70000),
                                offset = Offset(x = 5f, y = 4f),
                                blurRadius = 2f
                            )
                        )
                    )
                }
                Row {

                }
            }
        }
    }
    else {
        Text(text = "Approksimerbar")
        Icon(Icons.Filled.ArrowBack, "contentDescription", Modifier.clickable { viewDetails = !viewDetails })
    }
}

@Composable
fun NetworkResponseUI() {
    var response by remember { mutableStateOf("") }

    // Declaring a boolean value to store
    // the expanded state of the Text Field
    var mExpanded by remember { mutableStateOf(false) }

    // Create a list of cities
    val mScreens = listOf("Log in/Sign up", "Bars", "Map", "Bar crawl")

    // Create a string value to store the selected city
    var mSelectedText by remember { mutableStateOf(mScreens[1]) }

    var mTextFieldSize by remember { mutableStateOf(Size.Zero)}

    // Up Icon when expanded and down icon when collapsed
    val color = if (mExpanded)
        Color(0xFFFAE6C8)
    else
        Color(0xFFFFF0D2)

    // Perform the network operation in a coroutine
    LaunchedEffect(true) {
        val result = withContext(Dispatchers.IO) {
            makeNetworkRequest()
        }

        // Update the state variable with the response
        if (result != null) {
            response = result as String
        }
    }

    // Display the response in a Text composable
    Column {
        Row(
            Modifier
                .fillMaxWidth()
                .height(15.dp)
                .background(Color(0xFF014C2D))){
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(Color(0xFFCAA800))){
        }
        Row(
            Modifier
                .background(Color(0xFFB90000))
                .fillMaxWidth()
                .height(50.dp)) {
            Icon(Icons.Filled.Menu, "contentDescription",
                Modifier
                    .clickable { mExpanded = !mExpanded }
                    .size(50.dp),
                color
            )
            Row(
                Modifier
                    .fillMaxSize()
                    .offset(x = (-30).dp, y = 1.dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
                Text(text = mSelectedText, style = TextStyle(
                    color = Color(0xFFFFF0D2), fontSize = 35.sp, fontWeight = FontWeight.Bold,
                shadow = Shadow(color = Color(0xFF000000), offset = Offset(x = 2f, y = 2f), blurRadius = 1f)))
            }

            DropdownMenu(
                expanded = mExpanded,
                onDismissRequest = { mExpanded = false },
                modifier = Modifier
                    .width(200.dp)
                    //.border(width = 1.dp, color = Color(0xFF337800))
                    .background(color = Color(0xFFB9CDFF))
            ) {
                mScreens.forEach { label ->
                    DropdownMenuItem(onClick = {
                        mSelectedText = label
                        mExpanded = false
                    }) {
                        Text(text = label, style = TextStyle(color = Color.Black, fontSize = 20.sp))
                    }
                }
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(Color(0xFFCAA800))){
        }
        Column(Modifier.offset(y = 0.dp)) {
            if (mSelectedText == mScreens[1]) {
                Bars(response)
            }
            if (mSelectedText == mScreens[2]){
                MapScreen()
            }
        }





    }
}