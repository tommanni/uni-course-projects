package com.example.harjoitus_15_16

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Looper
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import com.example.harjoitus_15_16.ui.theme.Harjoitus_1516Theme
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState

class MainActivity : ComponentActivity() {

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var locationCallback: LocationCallback
    private lateinit var viewModel: MainViewModel

    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {
                viewModel.hasPermission = true
            }
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {
                viewModel.hasPermission = true
            } else -> {
                viewModel.hasPermission = false
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        locationCallback = object : LocationCallback() {
            override fun onLocationResult(locationResult: LocationResult) {
                locationResult.lastLocation?.let { viewModel.setLastKnownLocation(it) }
            }
        }

        viewModel = MainViewModel(applicationContext.filesDir, fusedLocationClient)

        setContent {
            Harjoitus_1516Theme {
                requestLocationPermission()

                // A surface container using the 'background' color from the theme
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        topBar = { TopAppBar(title = { Text(text = "Location App") }) }
                    ) { paddingValues ->
                        LocationAppBody(
                            viewModel = viewModel,
                            hasPermissions = viewModel.hasPermission,
                            modifier = Modifier.padding(paddingValues)
                        )
                    }
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (!viewModel.requestingLocationUpdates) startLocationUpdates()
    }

    override fun onPause() {
        super.onPause()
        stopLocationUpdates()
    }

    private fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)
        viewModel.requestingLocationUpdates = false
    }

    private fun startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestLocationPermission()
            return
        }
        fusedLocationClient.requestLocationUpdates(
            LocationRequest.Builder(10000).build().apply {
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
                interval = 10000
                fastestInterval = 5000
            },
            locationCallback,
            Looper.getMainLooper())
        viewModel.requestingLocationUpdates = true
    }

    private fun requestLocationPermission() {
        locationPermissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION))
    }
}

@Composable
fun LocationAppBody(
    viewModel: MainViewModel,
    hasPermissions: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.fillMaxSize()
    ) {
        if(!hasPermissions) {
            Text(
                text = "Permission is not granted. Cannot access location",
                modifier = Modifier.padding(16.dp)
            )
        } else {
            LocationMap(
                viewModel = viewModel,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
fun LocationMap(
    viewModel: MainViewModel,
    modifier: Modifier = Modifier
) {
    val uiState = viewModel.uiState.collectAsState()
    val lastKnownLocation = uiState.value.lastKnownLocation
    val locationData = uiState.value.locationData
    val currentPosition = LatLng(lastKnownLocation.latitude, lastKnownLocation.longitude)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentPosition, 15f)
    }
    val bitmap = BitmapFactory.decodeResource(LocalContext.current.resources, R.drawable.current_location)
    Box(modifier = modifier.fillMaxSize()) {
        GoogleMap(
            modifier = modifier,
            cameraPositionState = cameraPositionState
        ){
            Marker(
                state = MarkerState(position = currentPosition),
                icon = BitmapDescriptorFactory.fromBitmap(bitmap),
                anchor = Offset(0.5f, 0.5f),
                contentDescription = "Current location marker"
            )
            locationData.forEach { location ->
                val position = LatLng(location.latitude, location.longitude)
                Marker(
                    state = MarkerState(position = position),
                    contentDescription = "Saved location"
                )
            }
        }
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            Text(
                text = "Latitude: ${currentPosition.latitude}",
                color = Color.Black
            )
            Text(
                text = "Longitude: ${currentPosition.longitude}",
                color = Color.Black
            )
        }
        FilledIconButton(
            onClick = { viewModel.saveLocationData() },
            modifier = Modifier
                .width(160.dp)
                .padding(bottom = 60.dp)
                .align(Alignment.BottomCenter),
        ) {
            Text(text = "Save Location", fontWeight = FontWeight.Bold)
        }
    }
}