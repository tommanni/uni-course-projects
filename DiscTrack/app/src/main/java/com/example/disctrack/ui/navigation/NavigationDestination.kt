package com.example.disctrack.ui.navigation

/**
 * Interface to create navigation destinations
 */
interface NavigationDestination {
    // Route to the navigation destination
    val route: String
    // Title of the navigation destination, shown on topAppBar
    val titleRes: Int
}