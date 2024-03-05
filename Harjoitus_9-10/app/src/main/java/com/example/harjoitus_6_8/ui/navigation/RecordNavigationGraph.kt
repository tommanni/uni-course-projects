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
import com.example.harjoitus_6_8.ui.login.LoginDestination
import com.example.harjoitus_6_8.ui.login.LoginScreen
import com.example.harjoitus_6_8.ui.record.RecordDetailsDestination
import com.example.harjoitus_6_8.ui.record.RecordDetailsScreen
import com.example.harjoitus_6_8.ui.record.RecordEditDestination
import com.example.harjoitus_6_8.ui.record.RecordEditScreen
import com.example.harjoitus_6_8.ui.record.RecordEntryDestination
import com.example.harjoitus_6_8.ui.record.RecordEntryScreen
import com.example.harjoitus_6_8.ui.sing_up.SignInDestination
import com.example.harjoitus_6_8.ui.sing_up.SignInScreen

@Composable
fun RecordsNavHost(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = LoginDestination.route,
        modifier = modifier
    ) {
        composable(route = LoginDestination.route) {
            LoginScreen(
                navigateToSignup = { navController.navigate(SignInDestination.route) },
                navigateToHomeScreen = { navController.navigate(HomeDestination.route) }
            )
        }
        composable(route = SignInDestination.route) {
            SignInScreen(
                navigateUp = { navController.navigateUp() },
                navigateBack = { navController.popBackStack() }
            )
        }
        composable(route = HomeDestination.route) {
            HomeScreen(
                navigateToRecordEntry = { navController.navigate(RecordEntryDestination.route)},
                navigateToRecordUpdate = {
                    navController.navigate("${RecordDetailsDestination.route}/${it}")
                },
                navigateToLogin = { navController.navigate(LoginDestination.route) {
                    popUpTo(LoginDestination.route) {
                        inclusive = true
                    }
                } }
            )
        }
        composable(route = RecordEntryDestination.route) {
            RecordEntryScreen(
                navigateBack = { navController.navigate(HomeDestination.route) {
                    popUpTo(HomeDestination.route) {
                        inclusive = true
                    }
                } },
                onNavigateUp = { navController.navigateUp() }
            )
        }
        composable(
            route = RecordDetailsDestination.routeWithArgs,
            arguments = listOf(navArgument(RecordDetailsDestination.itemIdArg) {
                type = NavType.StringType
            })
        ) {
            RecordDetailsScreen(
                navigateToEditRecord = { navController.navigate("${RecordEditDestination.route}/${it}") },
                navigateBack = {
                    navController.navigate(HomeDestination.route) {
                        popUpTo(HomeDestination.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(
            route = RecordEditDestination.routeWithArgs,
            arguments = listOf(navArgument(RecordEditDestination.itemIdArg) {
                type = NavType.StringType
            })
        ) {
            RecordEditScreen(
                navigateBack = { navController.popBackStack() },
                onNavigateUp = { navController.navigateUp() }
            )
        }
    }
}