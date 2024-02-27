package com.example.harjoitus_6_8.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.harjoitus_6_8.ui.home.HomeDestination
import com.example.harjoitus_6_8.ui.home.HomeScreen
import com.example.harjoitus_6_8.ui.record.RecordDetailsDestination
import com.example.harjoitus_6_8.ui.record.RecordDetailsScreen
import com.example.harjoitus_6_8.ui.record.RecordEditDestination
import com.example.harjoitus_6_8.ui.record.RecordEditScreen
import com.example.harjoitus_6_8.ui.record.RecordEntryDestination
import com.example.harjoitus_6_8.ui.record.RecordEntryScreen

@Composable
fun RecordsNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = HomeDestination.route,
        modifier = modifier
    ) {
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToRecordEntry = { navController.navigate(RecordEntryDestination.route)},
                navigateToRecordUpdate = {
                    navController.navigate("${RecordDetailsDestination.route}/${it}")
                }
            )
        }
        composable(route = RecordEntryDestination.route) {
            RecordEntryScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(
            route = RecordDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(RecordDetailsDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            RecordDetailsScreen(
                navigateToEditRecord = { navController.navigate("${RecordEditDestination.route}/${it}") },
                navigateBack = { navController.navigateUp() }
            )
        }
        composable(
            route = RecordEditDestination.routeWithArgs,
            arguments = listOf(navArgument(RecordEditDestination.itemIdArg) {
                type = NavType.IntType
            })
        ) {
            RecordEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}