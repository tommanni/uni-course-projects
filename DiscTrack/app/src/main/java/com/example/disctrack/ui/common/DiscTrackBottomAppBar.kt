package com.example.disctrack.ui.common

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.outlined.BarChart
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Map
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.disctrack.ui.courses.CoursesDestination
import com.example.disctrack.ui.home.HomeDestination
import com.example.disctrack.ui.statistics.StatisticsDestination

/**
 * Bottom navigation bar
 */
@Composable
fun DiscTrackBottomAppBar(
    navController: NavController,
    modifier: Modifier = Modifier
) {
    // Navigation bar items shown on the navigation bar
    val items = listOf(
        BottomNavigationItem(
            title = "Home",
            navigationDestination = HomeDestination.route,
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        BottomNavigationItem(
            title = "Courses",
            navigationDestination = CoursesDestination.route,
            selectedIcon = Icons.Filled.Map,
            unselectedIcon = Icons.Outlined.Map
        ),
        BottomNavigationItem(
            title = "Statistics",
            navigationDestination = StatisticsDestination.route,
            selectedIcon = Icons.Filled.BarChart,
            unselectedIcon = Icons.Outlined.BarChart
        )
    )

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    // Current selected navigation item
    val currentDestination = navBackStackEntry?.destination

    // Bottom navigation bar to navigate between screens
    NavigationBar {
        items.forEachIndexed { index, item ->
            // Tracks if current navigation item is selected
            val selected = currentDestination?.hierarchy?.any {
                it.route == item.navigationDestination
            } == true
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(item.navigationDestination) {
                        // Pop up to the start destination of the graph to
                        // avoid building up a large stack of destinations
                        // on the back stack as users select items
                        popUpTo(navController.graph.findStartDestination().id) {
                            saveState = true
                        }
                        // Avoid multiple copies of the same destination when
                        // reselecting the same item
                        launchSingleTop = true
                        // Restore state when reselecting a previously selected item
                        restoreState = true
                    }
                          },
                label = {
                    Text(
                        text = item.title,
                        // Highlight the selected navigation item icon
                        color = if (selected) {
                            Color.Green
                        } else {
                            Color.White
                        }
                    )
                },
                icon = {
                    Icon(
                        // Filled icon for selected item, unfilled for unselected
                        imageVector = if (selected)
                            item.selectedIcon
                        else
                            item.unselectedIcon,
                        contentDescription = "Home",
                        // Highlight the selected navigation item label
                        tint = if (selected) {
                            Color.Green
                        } else {
                            Color.LightGray
                        }
                    )
                }
            )
        }
    }
}

/**
 * Represents a navigation item shown on the navigation bar
 */
data class BottomNavigationItem(
    val title: String,
    val navigationDestination: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)