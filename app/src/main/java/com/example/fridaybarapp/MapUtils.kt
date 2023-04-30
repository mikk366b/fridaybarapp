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
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

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
fun MapScreen(onBackClicked: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Map Screen") },
                navigationIcon = {
                    val context = LocalContext.current
                    Button(onClick = { val intent = Intent(context, MainActivity::class.java)
                        ContextCompat.startActivity(context, intent, null)
                    }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"                        )
                    }
                }
            )
        },
        content = {
            val mapRef = rememberMapViewWithLifecycle()
            val mapView = rememberMapViewWithLifecycle()
            val fridayBars = listOf(
                FridayBar("Bar 1", LatLng(55.6761, 12.5683)),
                FridayBar("Bar 2", LatLng(55.6762, 12.5684)),
                FridayBar("Bar 3", LatLng(55.6763, 12.5685)),
                FridayBar("Bar 4", LatLng(56.6763, 13.5685))
            )
            val context = LocalContext.current
            AndroidView({mapView}) {mapView->
                CoroutineScope(Dispatchers.Main).launch {
                    val map = mapView.awaitMap()
                    map.uiSettings.isZoomControlsEnabled = true
                    val placeName = "approksimerbar"

                    val geocodehelper = GeocodeHelper(apiKey = "AIzaSyCQoksz4IDUyavwb4TU3U5JdpPMyXbzPSE",
                        context = context
                    )
                    val location = geocodehelper.geocode(placeName)
                    if (location != null) {
                        geocodehelper.addMarker(map, location, placeName)
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 14f))
                    }

                    val pickUp =  LatLng(-35.016, 143.321)
                    val destination = LatLng(-32.491, 147.309)
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(55.6763, 12.5685),6f))


                    fridayBars.forEach { fridayBar ->
                        val fridayBarMarkerOptions = MarkerOptions()
                            .position(fridayBar.location)
                            .title(fridayBar.name)

                        map.addMarker(fridayBarMarkerOptions)}

                }
            }
        }
    )
}
class MapActivity : AppCompatActivity() {

    private lateinit var mapFragment: SupportMapFragment
    private lateinit var map: GoogleMap

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.layout_map)

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync { googleMap ->
            map = googleMap
            map.uiSettings.isZoomControlsEnabled = true


            val fridayBars = listOf(
                FridayBar("Bar 1", LatLng(55.6761, 12.5683)),
                FridayBar("Bar 2", LatLng(55.6762, 12.5684)),
                FridayBar("Bar 3", LatLng(55.6763, 12.5685)),
                FridayBar("Bar 4", LatLng(56.6763, 13.5685))
            )

            val pickUp = LatLng(-35.016, 143.321)
            val destination = LatLng(-32.491, 147.309)
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(55.6763, 12.5685), 6f))

            fridayBars.forEach { fridayBar ->
                val fridayBarMarkerOptions = MarkerOptions()
                    .position(fridayBar.location)
                    .title(fridayBar.name)

                map.addMarker(fridayBarMarkerOptions)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mapFragment.onResume()
    }

    override fun onPause() {
        super.onPause()
        mapFragment.onPause()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapFragment.onDestroy()
    }

    override fun onLowMemory() {
        super.onLowMemory()
        mapFragment.onLowMemory()
    }
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