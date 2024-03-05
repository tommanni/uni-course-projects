package com.example.harjoitus_6_8.ui.navigation

interface NavigationDestination {
    // Route to the navigation destination
    val route: String
    // Title of the navigation destination, shown on topAppBar
    val titleRes: Int
}