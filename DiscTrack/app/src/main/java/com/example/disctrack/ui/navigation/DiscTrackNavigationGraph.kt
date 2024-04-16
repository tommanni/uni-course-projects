package com.example.disctrack.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import com.example.disctrack.ui.common.DiscTrackBottomAppBar
import com.example.disctrack.ui.courses.CoursesDestination
import com.example.disctrack.ui.courses.CoursesScreen
import com.example.disctrack.ui.home.HomeDestination
import com.example.disctrack.ui.home.HomeScreen
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.getValue
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.disctrack.ui.home.RoundSetupDestination
import com.example.disctrack.ui.home.RoundSetupScreen
import com.example.disctrack.ui.statistics.StatisticsDestination
import com.example.disctrack.ui.statistics.StatisticsScreen


@Composable
fun DiscTrackNavHost(
    navController: NavHostController,
    hasLocationPermission: Boolean,
    modifier: Modifier = Modifier
) {
    val backStackEntry by navController.currentBackStackEntryAsState()

    Scaffold(
        bottomBar = {
            if (backStackEntry?.destination?.route == HomeDestination.route ||
                backStackEntry?.destination?.route == CoursesDestination.route ||
                backStackEntry?.destination?.route == StatisticsDestination.route
                ) {
                DiscTrackBottomAppBar(navController = navController)
            }
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = HomeDestination.route,
            modifier = modifier.padding(it)
        ) {
            composable(route = HomeDestination.route) {
                HomeScreen(
                    navController = navController
                )
            }
            composable(route = CoursesDestination.route) {
                CoursesScreen(
                    hasLocationPermission = hasLocationPermission
                )
            }
            composable(route = StatisticsDestination.route) {
                StatisticsScreen()
            }
            composable(route = RoundSetupDestination.route) {
                RoundSetupScreen(
                    navController = navController
                )
            }
        }
    }
}