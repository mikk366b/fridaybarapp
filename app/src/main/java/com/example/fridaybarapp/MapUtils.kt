package com.example.fridaybarapp

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.google.android.libraries.maps.CameraUpdateFactory
import com.google.android.libraries.maps.GoogleMap
import com.google.android.libraries.maps.MapView
import com.google.android.libraries.maps.SupportMapFragment
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.android.Context
import android.content.Context.LOCATION_SERVICE
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.pm.PackageManager
import android.graphics.Color
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.MultiplePermissionsState
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.libraries.maps.model.*
import com.google.maps.errors.ApiException
import com.google.maps.internal.PolylineEncoding
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode

import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import java.io.IOException

val apiKey = "AIzaSyCQoksz4IDUyavwb4TU3U5JdpPMyXbzPSE"
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



enum class PermissionState {
    IDLE, // Initial state before any request is made
    GRANTED, // Permission granted
    DENIED // Permission denied
}
@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MapScreen() {
    val context = LocalContext.current
    val geocodehelper = GeocodeHelper(apiKey = apiKey, context = context)
    val fusedLocationClient =
        remember(context) { LocationServices.getFusedLocationProviderClient(context) }
    val currentLocationState = remember { mutableStateOf<Location?>(null) }
    val routeState = remember { mutableStateOf<PolylineOptions?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val permissionState = remember { mutableStateOf(PermissionState.IDLE) }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            permissionState.value = PermissionState.GRANTED
        } else {
            permissionState.value = PermissionState.DENIED
        }
    }


    Scaffold {
        val mapView = rememberMapViewWithLifecycle()
        AndroidView({ mapView }) { mapView ->
            coroutineScope.launch {
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
                        if (location != null) {
                            val latLng = location
                            geocodehelper.addMarker(map, latLng, name, "test")
                            val googlplexlatlng = LatLng(37.421519, -122.088715)
                            geocodehelper.addMarker(map,googlplexlatlng,"test","test")
                            if (i == 1) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
                            }
                        }
                    }
                }

                // Request location permission and get the user's current location
                if (ContextCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION)
                    == PERMISSION_GRANTED
                ) {
                    // Access to the location is already granted
                    try {
                        val location = fusedLocationClient.lastLocation.await()
                        currentLocationState.value = location
                        geocodehelper.addMarker(
                            map,
                            LatLng(location.latitude, location.longitude),
                            "Current Location",
                            "test"
                        )
                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Failed to get current location",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // Request access to the location
                    when (permissionState.value) {
                        PermissionState.IDLE -> {
                            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                        }
                        PermissionState.GRANTED -> {
                            // Permission has been granted by the user
                            try {
                                val location = fusedLocationClient.lastLocation.await()
                                currentLocationState.value = location
                                geocodehelper.addMarker(
                                    map,
                                    LatLng(location.latitude, location.longitude),
                                    "Current Location",
                                    "test"
                                )
                            } catch (e: Exception) {
                                Toast.makeText(
                                    context,
                                    "Failed to get current location",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                        PermissionState.DENIED -> {
                            // Permission has been denied by the user
                            Toast.makeText(
                                context,
                                "Location access is required to use this feature",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                    // Add a "Get route" button to the map marker info window
                    map.setOnMarkerClickListener { marker ->
                        if (marker.title != "Current Location") {
                            coroutineScope.launch {
                                val currentLocation = currentLocationState.value
                                if (currentLocation != null) {
                                    val route = geocodehelper.getRoute(
                                        LatLng(currentLocation.latitude, currentLocation.longitude),
                                        marker.position
                                    )
                                    routeState.value = route as PolylineOptions?
                                } else {
                                    Toast.makeText(
                                        context,
                                        "Failed to get current location",
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                        }
                        false
                    }

                    // Draw the route on the map
                    val route = routeState.value
                    if (route != null) {
                        map.addPolyline(route)
                    }
                }
            }
        }
    }
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

    fun addMarker(map: GoogleMap, position: LatLng, title: String?, snippet: String) {
        map.addMarker(
            MarkerOptions()
                .position(position)
                .title(title)
                .snippet(snippet)
        )

    }
    suspend fun getRoute(origin: LatLng, destination: LatLng): Any? = withContext(Dispatchers.IO) {
        try {
            val result: DirectionsResult = DirectionsApi.newRequest(geoApiContext)
                .mode(TravelMode.WALKING)
                .origin(origin.latitude.toString() + "," + origin.longitude.toString())
                .destination(destination.latitude.toString() + "," + destination.longitude.toString())
                .await()

            if (result.routes.isNotEmpty()) {
                val route = result.routes[0]
                val overviewPolyline = route.overviewPolyline
                if (overviewPolyline != null) {
                    val points = overviewPolyline.decodePath()
                    val polyOptions = PolylineOptions()
                    points.forEach {
                        polyOptions.add(LatLng(it.lat, it.lng))
                    }
                    return@withContext polyOptions
                }
            }
        } catch (e: ApiException) {
            Log.e("GeocodeHelper", "Error getting directions: ${e.message}")
        } catch (e: InterruptedException) {
            Log.e("GeocodeHelper", "Error getting directions: ${e.message}")
        } catch (e: IOException) {
            Log.e("GeocodeHelper", "Error getting directions: ${e.message}")
        }

        return@withContext null
    }
    fun getGeoContext(): GeoApiContext {
        val geoApiContext = GeoApiContext.Builder()
            .apiKey(apiKey)
            .build()
        return geoApiContext
    }
    fun getUserLocation(): LatLng? {
        val locationManager = context.getSystemService(
            Context.getApplicationContext(
            ).toString()
        ) as? LocationManager
        if (ActivityCompat.checkSelfPermission(context, ACCESS_FINE_LOCATION) != PERMISSION_GRANTED
            && ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PERMISSION_GRANTED) {
            return null
        }
        val location = locationManager?.getLastKnownLocation(LocationManager.GPS_PROVIDER)
        return if (location != null) {
            LatLng(location.latitude, location.longitude)
        } else {
            null
        }
    }
}
