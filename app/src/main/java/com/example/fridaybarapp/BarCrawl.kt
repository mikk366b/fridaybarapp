package com.example.fridaybarapp
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.SupportMapFragment
import com.google.android.libraries.maps.model.LatLng
import com.google.android.libraries.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONArray
import androidx.compose.runtime.remember
import androidx.compose.material.Checkbox
import org.json.JSONObject
import android.util.Log

@Composable
fun BarCrawlScreen(
    response: String) {

    var bars = remember { mutableStateOf(mutableListOf<JSONObject>()) }

    LaunchedEffect(Unit) {
        val JSONbars = makeNetworkRequestJSON()
        if (JSONbars != null) {
            for (i in 0 until JSONbars.length()) {
                bars.value.add(JSONbars.getJSONObject(i))
            }
        }
    }
    val tempBars = remember { mutableStateListOf("Bar A", "Bar B", "Bar C", "Bar D") }
    val randomizedBars = remember { mutableStateListOf<String>() }
    Button(
        onClick = { randomizedBars.clear()
            tempBars.shuffle()
            randomizedBars.addAll(tempBars.subList(0, 3)) },
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        colors = androidx.compose.material.ButtonDefaults.buttonColors(backgroundColor = Color(0xFFD2DF05))
    ) {
        Text(text = "Create BarCrawl")
    }



    Log.d("logic", (bars == null).toString())


    Log.d("temp", tempBars.toString())

    val randomBars = "hej"
    if (bars != null) {
        Log.v("barsize", bars.value.toString())
        //Log.v("myTag", bars.value[1].toString())
        for (i in 0 until randomizedBars.size) {
            val bar = randomizedBars[i]
            Spacer(modifier = Modifier.height(12.dp))
            Card(
                Modifier
                    .height(80.dp)
                    .width(390.dp)
                    .offset(x = 10.dp),
                shape = RoundedCornerShape(20),
                backgroundColor = Color(0xFF000000),
                border = BorderStroke(2.dp, color = Color(0xFFA36D00))
            ) {
                Column(Modifier.padding(15.dp)) {
                    CustomText(data = bar, fontSize = 25)
                    CustomText(data = bar, fontSize = 15)

                }

            }
        }
    }


}

