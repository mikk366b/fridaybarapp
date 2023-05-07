package com.example.fridaybarapp

import android.Manifest
import android.annotation.SuppressLint
import android.location.Location
import android.os.Bundle
import androidx.compose.material.*
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
import com.google.maps.DirectionsApi
import com.google.maps.GeoApiContext
import com.google.maps.GeocodingApi
import com.google.maps.android.ktx.awaitMap
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.Manifest.permission.ACCESS_FINE_LOCATION
import android.app.Activity
import android.content.Context
import android.content.pm.PackageManager.PERMISSION_GRANTED
import android.content.pm.PackageManager
import android.graphics.Color
import android.provider.ContactsContract.CommonDataKinds.Website.URL
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
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*

import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.libraries.maps.model.*
import com.google.maps.errors.ApiException
import com.google.maps.internal.PolylineEncoding
import com.google.maps.model.DirectionsResult
import com.google.maps.model.TravelMode

import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.net.URL

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
//@OptIn(ExperimentalPermissionsApi::class)
@SuppressLint("UnusedMaterialScaffoldPaddingParameter")
@Composable
fun MapScreen() {

    val context = LocalContext.current
    val geocodehelper = GeocodeHelper(apiKey = apiKey, context = context)
    val fusedLocationClient: FusedLocationProviderClient =
        remember(context) { LocationServices.getFusedLocationProviderClient(context) }
    val currentLocationState = remember { mutableStateOf<Location?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val permissionState = remember { mutableStateOf(PermissionState.IDLE) }
    val selectedMarkerState = remember { mutableStateOf<Marker?>(null) }
    val currentLocationMarkerState = remember { mutableStateOf<Marker?>(null) }
    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            permissionState.value = PermissionState.GRANTED
        } else {
            permissionState.value = PermissionState.DENIED
        }
    }
    val mapReadyState = remember { mutableStateOf(false) }

    // Define the updateUserLocation function here
    fun updateUserLocation(map: GoogleMap, location: Location) {
        val latLng = LatLng(location.latitude, location.longitude)
        currentLocationState.value = location

        if (currentLocationMarkerState.value == null) {
            val marker = geocodehelper.addMarker(
                map,
                latLng,
                "Current Location",
                "test",
                markerColor = BitmapDescriptorFactory.HUE_GREEN
            )
            currentLocationMarkerState.value = marker
        } else {
            currentLocationMarkerState.value?.position = latLng
        }

    }

    LaunchedEffect(permissionState.value, mapReadyState.value) {
        if (ContextCompat.checkSelfPermission(
                context,
                ACCESS_FINE_LOCATION
            ) == PERMISSION_GRANTED
        ) {
            // Access to the location is already granted
            try {
                val locationRequest = LocationRequest.create().apply {
                    priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                    interval = 5000 // Update location every 5 seconds
                }
            } catch (e: Exception) {
                Toast.makeText(
                    context,
                    "Failed to get current location, location already provided",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            // Request access to the location
            when (permissionState.value) {
                PermissionState.IDLE -> {
                    requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
                }
                PermissionState.GRANTED -> {
                    // Permission has been granted by the user
                    try {
                        val location = fusedLocationClient.lastLocation.await()
                        currentLocationState.value = location
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
        }
    }
    fun findClosestPoint(userLocation: LatLng, points: List<LatLng>): LatLng {
        var closestPoint = points[0]
        var smallestDistance = Float.MAX_VALUE

        for (point in points) {
            val distance = FloatArray(1)
            Location.distanceBetween(userLocation.latitude, userLocation.longitude, point.latitude, point.longitude, distance)
            if (distance[0] < smallestDistance) {
                smallestDistance = distance[0]
                closestPoint = point
            }
        }

        return closestPoint
    }
    val markerLatLngMap = remember { mutableMapOf<Marker, LatLng>() }
    val context2 = LocalContext.current
    val currentRoute = remember { mutableStateOf<Polyline?>(null) }
    val mapView = rememberMapViewWithLifecycle()

    Scaffold {
        AndroidView({ mapView }) { mapView ->
            coroutineScope.launch {
                val map = mapView.awaitMap()
                map.uiSettings.isZoomControlsEnabled = true
                val JSONbars = makeNetworkRequestJSON()
                val bars = JSONbars
                val googleplexcoord = LatLng(37.42106013828215, -122.08605307914131)
                map.setOnMarkerClickListener { marker ->
                    marker.showInfoWindow()
                    true
                }
                if (bars != null) {
                    for (i in 0 until bars.length()) {
                        val bar = bars.getJSONObject(i)
                        val name = bar.getString("name")
                        val address = bar.getString("address")
                        val location = geocodehelper.geocode(address)

                        if (location != null) {
                            val latLng = location
                            val marker = geocodehelper.addMarker(map, latLng, name, "test")
                            markerLatLngMap[marker] = latLng
                            val marker2 = geocodehelper.addMarker(map,googleplexcoord , "googleplextest", "test")
                            markerLatLngMap[marker2] = googleplexcoord

                            map.setOnMarkerClickListener { clickedMarker ->
                                currentRoute.value?.remove()
                                val destination = markerLatLngMap[clickedMarker]
                                if (destination != null) {
                                    val origin = currentLocationState.value
                                    if (origin != null) {
                                        val originLatLng = LatLng(origin.latitude, origin.longitude)
                                        coroutineScope.launch {
                                            val directionsResult = geocodehelper.getDirections(originLatLng, destination)
                                            if (directionsResult != null) {
                                                val polylineOptions = PolylineOptions().addAll(directionsResult).color(Color.BLUE)
                                                val polyline = map.addPolyline(polylineOptions)
                                                currentRoute.value = polyline

                                            } else {
                                                Toast.makeText(context, "Failed to get directions", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } else {
                                        Toast.makeText(context, "Current location not available", Toast.LENGTH_SHORT).show()
                                    }
                                }
                                marker.showInfoWindow()
                                return@setOnMarkerClickListener false
                            }



                            if (i == 1) {
                                map.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 12f))
                            }
                        }
                    }
                }

                // Request location permission and get the user's current location

                if (ContextCompat.checkSelfPermission(
                        context,
                        ACCESS_FINE_LOCATION
                    ) == PERMISSION_GRANTED
                ) {
                    // Access to the location is already granted
                    try {
                        val locationRequest = LocationRequest.create().apply {
                            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                            interval = 5000 // Update location every 5 seconds
                        }

                        val locationCallback = object : LocationCallback() {
                            override fun onLocationResult(locationResult: LocationResult?) {
                                locationResult?.lastLocation?.let { location ->
                                    updateUserLocation(map, location)
                                    val latLng = LatLng(location.latitude, location.longitude)
                                    currentLocationState.value = location
                                    val clickedMarker = selectedMarkerState.value
                                    if (clickedMarker != null) {
                                        val destination = markerLatLngMap[clickedMarker]
                                        if (destination != null) {
                                            currentRoute.value?.remove()
                                            coroutineScope.launch {
                                                val directionsResult = geocodehelper.getDirections(latLng, destination)
                                                if (directionsResult != null) {
                                                    val polylineOptions = PolylineOptions().addAll(directionsResult).color(Color.BLUE)
                                                    val polyline = map.addPolyline(polylineOptions)
                                                    currentRoute.value = polyline

                                                } else {
                                                    Toast.makeText(context, "Failed to get directions", Toast.LENGTH_SHORT).show()
                                                }
                                            }
                                        }
                                    }
                                    updateUserLocation(map, location)
                                }
                            }
                        }

                        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null)

                    } catch (e: Exception) {
                        Toast.makeText(
                            context,
                            "Failed to get current location, location already provided",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    // Request access to the location
                    when (permissionState.value) {
                        PermissionState.IDLE -> {
                            requestPermissionLauncher.launch(ACCESS_FINE_LOCATION)
                        }
                        PermissionState.GRANTED -> {
                            // Permission has been granted by the user
                            try {
                                val location = fusedLocationClient.lastLocation.await()
                                currentLocationState.value = location
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
    suspend fun getDirections(origin: LatLng, destination: LatLng): List<LatLng>? {
        val directionsUrl = "https://maps.googleapis.com/maps/api/directions/json" +
                "?origin=${origin.latitude},${origin.longitude}" +
                "&destination=${destination.latitude},${destination.longitude}" +
                "&key=$apiKey"

        val result = try {
            val response = withContext(Dispatchers.IO) { URL(directionsUrl).readBytes() }
            val jsonResult = String(response)
            val jsonObject = JSONObject(jsonResult)
            val routes = jsonObject.getJSONArray("routes")
            val legs = routes.getJSONObject(0).getJSONArray("legs")
            val steps = legs.getJSONObject(0).getJSONArray("steps")
            val polylineList = mutableListOf<LatLng>()
            if (routes.length() > 0){
            for (i in 0 until steps.length()) {
                val encodedPolyline = steps.getJSONObject(i).getJSONObject("polyline").getString("points")
                val decodedPolyline = decodePolyline(encodedPolyline)
                polylineList.addAll(decodedPolyline)
            }
            }else {
                Log.e("GeocodeHelper", "No routes found")
                return null
            }
            polylineList
        } catch (e: Exception) {
            Log.e("GeocodeHelper", "Failed to get directions", e)
            null
        }

        return result
    }

    private fun decodePolyline(encodedPolyline: String): List<LatLng> {
        val polyline = mutableListOf<LatLng>()
        var index = 0
        var lat = 0
        var lng = 0

        while (index < encodedPolyline.length) {
            var shift = 0
            var result = 0
            do {
                val b = encodedPolyline[index++].code - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlat = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lat += dlat

            shift = 0
            result = 0
            do {
                val b = encodedPolyline[index++].toInt() - 63
                result = result or (b and 0x1f shl shift)
                shift += 5
            } while (b >= 0x20)
            val dlng = if (result and 1 != 0) (result shr 1).inv() else result shr 1
            lng += dlng

            val latLng = LatLng(lat.toDouble() / 1E5, lng.toDouble() / 1E5)
            polyline.add(latLng)
        }

        return polyline
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

    fun addMarker(map: GoogleMap, latLng: LatLng, title: String, snippet: String, markerColor: Float? = null): Marker {
        val markerOptions = MarkerOptions()
            .position(latLng)
            .title(title)
            .snippet(snippet)

        if (markerColor != null) {
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(markerColor))
        }

        return map.addMarker(markerOptions)
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
    private val REQUEST_LOCATION_PERMISSION_CODE = 1001
    fun getUserLocation(context: Context, callback: (LatLng?) -> Unit) {
        val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)
        val locationRequest = LocationRequest.create().apply {
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        }

        val permission = Manifest.permission.ACCESS_FINE_LOCATION

        if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            // Request permission
            callback(null)
            return
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult?) {
                locationResult?.lastLocation?.let { location ->
                    val latLng = LatLng(location.latitude, location.longitude)
                    callback(latLng)
                } ?: run {
                    callback(null)
                }
                fusedLocationClient.removeLocationUpdates(this) // Remove the updates listener
            }
        }, null)
    }
}
