package com.example.disctrack

import android.Manifest
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.pm.ActivityInfo
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.disctrack.data.repository.WorkManagerCourseDataSyncRepository
import com.example.disctrack.ui.theme.DiscTrackTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Launcher to ask for permissions
    private val locationPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        when {
            permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false) -> {}
            permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false) -> {}
            else -> {}
        }
    }


    @SuppressLint("SourceLockedOrientationActivity")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Limit screen orientation to portrait only
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setContent {
            DiscTrackTheme {
                // If permission hasn't been given yet, show location permission rationale
                var showLocationPermissionRationale by  remember { mutableStateOf(
                    !hasLocationPermission()
                ) }
                if (showLocationPermissionRationale) {
                    LocationPermissionRationaleDialog(
                        onGrantPermission = { requestLocationPermission() },
                        onCancel = { showLocationPermissionRationale = false },
                        onPermissionGranted = { showLocationPermissionRationale = false }
                    )
                }
                DiscTrackApp()
            }
        }
    }

    // Request permissions for fine and coarse locations
    private fun requestLocationPermission() {
        locationPermissionLauncher.launch(arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION)
        )
    }

    // Checks if user has already granted location permission for fine location
    private fun hasLocationPermission(): Boolean {
        return ActivityCompat.checkSelfPermission(
            this,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED
    }

}

// Location permission rationale dialog to inform user why location permission is needed
@Composable
fun LocationPermissionRationaleDialog(
    onGrantPermission: () -> Unit,
    onPermissionGranted: () -> Unit,
    onCancel: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onCancel,
        title = {
            Text(
                text = "Location Permission Required",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "This app requires access to your device's location to provide the" +
                            "following features:",
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(16.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Map,
                        contentDescription = "Icon for map",
                        tint = Color.Green
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Map: to show your location on the map",
                        fontSize = 14.sp
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.List,
                        contentDescription = "Icon for list",
                        tint = Color.Green
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Courses list: to show your local disc golf courses",
                        fontSize = 14.sp
                    )
                }
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Settings,
                        contentDescription = "Icon for settings",
                        tint = Color.Green
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Round setup: to show your local disc golf courses",
                        fontSize = 14.sp
                    )
                }
                Spacer(Modifier.height(16.dp))
                Text(
                    text = "Please grant the location permission to enjoy the full functionality of the app.",
                    fontSize = 16.sp
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onGrantPermission()
                    onPermissionGranted()
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.Green
                )
            ) {
                Text("Grant Permission")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onCancel,
                colors = ButtonDefaults.textButtonColors(
                    contentColor = Color.Gray
                )
            ) {
                Text("Cancel")
            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}