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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextIndent
import androidx.compose.ui.unit.*
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.PopupProperties
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
import okhttp3.OkHttpClient
import okhttp3.Request
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
                Surface(modifier = Modifier.fillMaxSize(), color = Color(0xFF014C05)) {
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
    var barName by remember { mutableStateOf("") }
    val mBars = listOf("A bar", "B bar", "C bar", "D bar", "E bar")
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
            .height(1.dp)
            .background(Color(0xFF000000))){
    }
    Row(
        Modifier
            .fillMaxWidth()
            .height(2.5.dp)
            .background(Color(0xFFF5B633))){
    }
    Row(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFF000000))){
    }
    Row(
        Modifier
            .height(35.dp)
            .fillMaxWidth()
            .background(Color(0xFFB90000))) {
        if(!viewDetails) {
            Text(
                text = "List of friday bars", Modifier.offset(x = 10.dp, y = -(2).dp),
                style = TextStyle(
                    color = Color(0xFFFFF0D2),
                    fontSize = 25.sp,
                    fontWeight = FontWeight.SemiBold,
                    shadow = Shadow(
                        color = Color(0xFF000000),
                        offset = Offset(x = 2f, y = 2f),
                        blurRadius = 1f
                    )
                )
            )
        }
        else {
            Icon(Icons.Filled.KeyboardArrowLeft, "contentDescription",
                Modifier
                    .clickable { viewDetails = !viewDetails }
                    .size(35.dp)
                    .offset(x = 0.dp),
                tint = Color(0xFFFFF0D2)
                )
            Text(
                text = barName, Modifier.offset(x = 2.dp, y = -(0).dp),
                style = TextStyle(
                    color = Color(0xFFFFF0D2),
                    fontSize = 25.sp,
                    fontWeight = FontWeight.SemiBold,
                    shadow = Shadow(
                        color = Color(0xFF000000),
                        offset = Offset(x = 2f, y = 2f),
                        blurRadius = 1f
                    )
                )
            )
        }
    }
    Row(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFF000000))){
    }
    Row(
        Modifier
            .fillMaxWidth()
            .height(2.5.dp)
            .background(Color(0xFFF5B633))){
    }
    Row(
        Modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(Color(0xFF000000))){
    }
    if(!viewDetails) {


        //Text(text = response)
        Log.v("JSON response før", response)
        val parts = response.lines()
        for (i in 0..4) {
            Spacer(modifier = Modifier.height(10.dp))
            Card(
                Modifier
                    .height(80.dp)
                    .width(390.dp)
                    .offset(x = 10.dp)
                    .clickable {
                        viewDetails = !viewDetails
                        barName = mBars[i]
                    },
                shape = RoundedCornerShape(30),
                border = BorderStroke(2.dp, color = Color(0xFF000000)),
                backgroundColor = Color(0xFF968560)) {
                Card(Modifier.padding(4.dp),
                    shape = RoundedCornerShape(28),
                    backgroundColor = Color(0xFF000000),
                    border = BorderStroke(0.dp, color = Color(0xFF968560))) {
                    Column {
                        Text(
                            text = mBars[i],
                            Modifier.offset(x = 10.dp, y = 2.dp),
                            style = TextStyle(
                                color = Color(0xFFFFF0D2),
                                fontSize = 25.sp,
                                fontWeight = FontWeight.Bold,
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
                                fontWeight = FontWeight.Bold,
                                shadow = Shadow(
                                    color = Color(0xFFE70000),
                                    offset = Offset(x = 5f, y = 4f),
                                    blurRadius = 2f
                                )
                            )
                        )
                    }
                }
            }

        }
    }
    else {
        Spacer(modifier = Modifier.height(5.dp))
        Card(Modifier
            .height(365.dp)
            .width(390.dp)
            .offset(x = 10.dp),
            shape = RoundedCornerShape(1),
            border = BorderStroke(2.dp, color = Color(0xFF000000)),
            backgroundColor = Color(0xFF014C05)
        ) {
            
        }
        Spacer(modifier = Modifier.height(5.dp))
        Card(
            Modifier
                .height(80.dp)
                .width(390.dp)
                .offset(x = 10.dp),
            shape = RoundedCornerShape(30),
            border = BorderStroke(2.dp, color = Color(0xFF000000)),
            backgroundColor = Color(0xFF968560)) {
            Card(Modifier.padding(4.dp),
                shape = RoundedCornerShape(28),
                backgroundColor = Color(0xFF000000),
                border = BorderStroke(0.dp, color = Color(0xFF968560))) {
                Column {
                    Text(
                        text = "Adresse",
                        Modifier.offset(x = 10.dp, y = 2.dp),
                        style = TextStyle(
                            color = Color(0xFFFFF0D2),
                            fontSize = 25.sp,
                            fontWeight = FontWeight.Bold,
                            shadow = Shadow(
                                color = Color(0xFFE70000),
                                offset = Offset(x = 5f, y = 4f),
                                blurRadius = 2f
                            )
                        )
                    )
                    Text(
                        text = "Facebook",
                        Modifier.offset(x = 10.dp, y = 2.dp),
                        style = TextStyle(
                            color = Color(0xFFFFE99B),
                            fontSize = 25.sp,
                            fontWeight = FontWeight.SemiBold,
                            textDecoration = TextDecoration.Underline,
                            shadow = Shadow(
                                color = Color(0xFF00FF22),
                                offset = Offset(x = 2f, y = 2f),
                                blurRadius = 0f
                            )
                        )
                    )
                }
            }
        }

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
        Color(0xFFFADCB4)
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
                .background(Color(0xFF014C05))){
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF000000))){
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(Color(0xFFF5B633))){
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF000000))){
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
                    .offset(x = (-27).dp, y = -(3).dp), horizontalArrangement = Arrangement.Center, verticalAlignment = Alignment.CenterVertically){
                Text(text = mSelectedText,
                    style = TextStyle(color = Color(0xFFFFF0D2),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.SemiBold,
                        shadow = Shadow(color = Color(0xFF000000), offset = Offset(x = 3f, y = 3f), blurRadius = 1f),
                        letterSpacing = 3.sp
                    ))
            }

            DropdownMenu(
                expanded = mExpanded,
                onDismissRequest = { mExpanded = false },
                modifier = Modifier
                    .width(300.dp)
                    .border(width = 3.dp, color = Color(0xFF000000), shape = RoundedCornerShape(3))
                    .background(color = Color(0xFF014C05)),
                offset = DpOffset(x = 2.dp, y = 4.dp)
            ) {
                mScreens.forEach { label ->
                    DropdownMenuItem(onClick = {
                        mSelectedText = label
                        mExpanded = false
                    }, modifier = Modifier.sizeIn(minHeight = 80.dp),
                    contentPadding = PaddingValues(0.dp)) {
                        Text(text = label,
                            modifier = Modifier
                                .border(1.5.dp, color = Color(0xFFF5B633))
                                .fillMaxWidth()
                                .background(color = Color(0xFFB90000))
                                .offset(y = -(3).dp),
                            lineHeight = 5.sp,
                            style = TextStyle(
                                color = Color(0xFFFFF0D2), fontSize = 30.sp, fontWeight = FontWeight.SemiBold,
                                shadow = Shadow(color = Color(0xFF000000), offset = Offset(x = 2f, y = 2f), blurRadius = 1f),
                                textDecoration = TextDecoration.Underline,
                                textIndent = TextIndent(firstLine = 15.sp)
                            ))
                    }
                }
            }
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF000000))){
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(Color(0xFFF5B633))){
        }
        Row(
            Modifier
                .fillMaxWidth()
                .height(1.dp)
                .background(Color(0xFF000000))){
        }
        Column(Modifier.offset(y = 0.dp)) {
            if (mSelectedText == mScreens[1]) {
                Bars(response)
            }
            if (mSelectedText == mScreens[2]) {
                Bars(response)
            }
        }





    }
}