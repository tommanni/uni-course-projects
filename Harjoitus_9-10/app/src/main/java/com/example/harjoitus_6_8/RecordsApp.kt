package com.example.harjoitus_6_8

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarScrollBehavior
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.example.harjoitus_6_8.ui.navigation.RecordsNavHost


/**
 * Top level composable that represents screens for the application.
 */
@Composable
fun RecordsApp(navController: NavHostController = rememberNavController()) {
    RecordsNavHost(navController)
}

/**
 * App bar to display title and conditionally display the back navigation
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecordsTopAppBar(
    title: String,
    canNavigateBack: Boolean,
    canLogout: Boolean = false,
    navigateUp: () -> Unit = {},
    onLogout: () -> Unit = {},
    scrollBehavior: TopAppBarScrollBehavior? = null
) {
    CenterAlignedTopAppBar(
        title = { Text(text = title) },
        navigationIcon =  {
            if (canNavigateBack) {
                IconButton(onClick = navigateUp) {
                    Icon(
                        imageVector = Icons.Filled.ArrowBack,
                        contentDescription = stringResource(R.string.back_button)
                    )
                }
            }
        },
        scrollBehavior = scrollBehavior,
        actions = {
            if (canLogout) {
                IconButton(onClick = onLogout) {
                    Icon(imageVector = Icons.Default.ExitToApp, contentDescription = "Logout")
                }
            }
        }
    )
}