package com.example.disctrack.ui.home

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.disctrack.R
import com.example.disctrack.ui.navigation.NavigationDestination

object RoundSetupDestination: NavigationDestination {
    override val route: String = "setup"
    override val titleRes: Int = R.string.round_setup_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundSetupScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState = viewModel.homeUiState.collectAsState()

    Scaffold(
        topBar =  {
            TopAppBar(
                title = { Text(stringResource(R.string.round_setup_title)) },
                navigationIcon = {
                    IconButton(onClick = { navController.navigateUp() }) {
                        Icon(imageVector = Icons.Filled.Close, contentDescription = "Close setup icon")
                    }
                }
            )
        }
    ) {
        LazyColumn(modifier.padding(it)) {

        }
    }
}