package com.example.fridaybarapp

import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
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

@Composable
fun rememberMapViewWithLifecycle(): MapView {
    val context = LocalContext.current
    val mapView = remember {
        MapView(context).apply {
            id = R.id.map
        }
    }

    // Makes MapView follow the lifecycle of this composable
    val lifecycleObserver = rememberMapLifecycleObserver(mapView)
    val lifecycle = LocalLifecycleOwner.current.lifecycle
    DisposableEffect(lifecycle) {
        lifecycle.addObserver(lifecycleObserver)
        onDispose {
            lifecycle.removeObserver(lifecycleObserver)
        }
    }

    return mapView
}
@SuppressLint("RememberReturnType")
@Composable
fun rememberMapLifecycleObserver(mapView: MapView): LifecycleEventObserver =
    remember(mapView) {
        LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(Bundle())
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> throw IllegalStateException()
            }
        }
    }


@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
//onBackClicked: () -> Unit
fun MapScreen() {
    val context = LocalContext.current
    val geocodehelper = GeocodeHelper(apiKey = "AIzaSyCQoksz4IDUyavwb4TU3U5JdpPMyXbzPSE",
        context = context
    )
    Scaffold(

        content = {
            val mapView = rememberMapViewWithLifecycle()
            AndroidView({mapView}) {mapView->
                CoroutineScope(Dispatchers.Main).launch {
                    val map = mapView.awaitMap()
                    map.uiSettings.isZoomControlsEnabled = true

                    val JSONbars = makeNetworkRequestJSON()
                    val bars = JSONbars

                    if (bars != null) {
                        for (i in 0 until bars.length()) {
                            val bar = bars.getJSONObject(i)
                            val name = bar.getString("name")
                            val address = bar.getString("address")
                            val location = geocodehelper.geocode(address)
                            if (location!=null) {
                                val latLng = location
                                geocodehelper.addMarker(map, latLng, name)
                                if (i == 1){
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))}
                            }

                        }

                    }


                }
            }
        }
    )
}

private fun getCurrentLocation(): Location {
// TODO: Implement getting current location
    return Location("")
}

class GeocodeHelper(private val context: android.content.Context, private val apiKey: String) {
    private val geoApiContext: GeoApiContext by lazy {
        GeoApiContext.Builder()
            .apiKey(apiKey)
            .build()
    }

    suspend fun geocode(placeName: String): LatLng? = withContext(Dispatchers.IO) {
        try {
            val results = GeocodingApi.geocode(geoApiContext, placeName).await()
            if (results.isNotEmpty()) {
                val location = results[0].geometry.location
                LatLng(location.lat, location.lng)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    fun addMarker(map: GoogleMap, position: LatLng, title: String?) {
        map.addMarker(
            MarkerOptions()
                .position(position)
                .title(title)
        )
    }
}