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
import com.example.disctrack.ui.statistics.StatisticsDestination
import com.example.disctrack.ui.statistics.StatisticsScreen


@Composable
fun DiscTrackNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    Scaffold(
        bottomBar = {
            DiscTrackBottomAppBar(navController = navController)
        }
    ) {
        NavHost(
            navController = navController,
            startDestination = HomeDestination.route,
            modifier = modifier.padding(it)
        ) {
            composable(route = HomeDestination.route) {
                HomeScreen()
            }
            composable(route = CoursesDestination.route) {
                CoursesScreen()
            }
            composable(route = StatisticsDestination.route) {
                StatisticsScreen()
            }
        }
    }
}