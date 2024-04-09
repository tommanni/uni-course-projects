package com.example.disctrack

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.disctrack.ui.navigation.DiscTrackNavHost

/**
 * Top level composable that represents screens for the application.
 */
@Composable
fun DiscTrackApp(
    navController: NavHostController = rememberNavController(),
    hasLocationPermission: Boolean,
) {
    DiscTrackNavHost(
        navController = navController,
        hasLocationPermission = hasLocationPermission
    )
}