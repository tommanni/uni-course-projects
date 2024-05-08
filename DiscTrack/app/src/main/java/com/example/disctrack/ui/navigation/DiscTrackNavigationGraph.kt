package com.example.disctrack.ui.navigation

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import com.example.disctrack.ui.common.DiscTrackBottomAppBar
import com.example.disctrack.ui.courses.CoursesDestination
import com.example.disctrack.ui.courses.CoursesScreen
import com.example.disctrack.ui.home.HomeDestination
import com.example.disctrack.ui.home.HomeScreen
import com.example.disctrack.ui.home.RoundSetupDestination
import com.example.disctrack.ui.home.RoundSetupScreen
import com.example.disctrack.ui.round.CreateCustomRoundDestination
import com.example.disctrack.ui.round.CreateCustomRoundScreen
import com.example.disctrack.ui.round.RoundTrackDestination
import com.example.disctrack.ui.round.RoundTrackScreen
import com.example.disctrack.ui.statistics.StatisticsDestination
import com.example.disctrack.ui.statistics.StatisticsScreen

/**
 * App NavHost and navigation graph
 */
@Composable
fun DiscTrackNavHost(
    navController: NavHostController,
    hasLocationPermission: Boolean,
    modifier: Modifier = Modifier
) {
    val backStackEntry by navController.currentBackStackEntryAsState()

    Scaffold(
        bottomBar = {
            // Show bottom bar only if current destination is one of the root destinations
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
                CoursesScreen()
            }
            composable(route = StatisticsDestination.route) {
                StatisticsScreen()
            }
            composable(route = RoundSetupDestination.route) {
                RoundSetupScreen(
                    navController = navController
                )
            }
            composable(route = CreateCustomRoundDestination.route) {
                CreateCustomRoundScreen(
                    navController = navController
                )
            }
            composable(
                route = RoundTrackDestination.route,
                arguments = listOf(navArgument("courseId") {
                    type = NavType.StringType
                })
            ) { backStackEntry ->
                RoundTrackScreen(
                    navController = navController,
                    backStackEntry.arguments?.getString("courseId"),
                    backStackEntry.arguments?.getString("courseName")
                )
            }
        }
    }
}