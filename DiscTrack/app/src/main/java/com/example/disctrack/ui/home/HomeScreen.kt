package com.example.disctrack.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material3.Card
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.disctrack.R
import com.example.disctrack.data.database.entities.Round
import com.example.disctrack.data.model.PlayedRound
import com.example.disctrack.ui.common.DiscTrackBottomAppBar
import com.example.disctrack.ui.navigation.NavigationDestination

object HomeDestination : NavigationDestination {
    override val route: String = "home"
    override val titleRes = R.string.app_name
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val homeUiState by viewModel.homeUiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text(text = "Home") })
                 },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { navController.navigate(RoundSetupDestination.route) },
                icon = {
                    Icon(
                        imageVector = Icons.Filled.AddCircle,
                        contentDescription = stringResource(R.string.start_a_round_button)
                    )
                       },
                text = {
                    Text(
                        text = stringResource(R.string.start_a_round_button),
                        fontWeight = FontWeight.Bold
                    )
                }
            )
        }
    ) {
        HomeBody(
            playedRounds = homeUiState.playedRounds,
            modifier = modifier.padding(it)
        )
    }
}

@Composable
fun HomeBody(
    playedRounds: List<PlayedRound>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
    ) {
        items(playedRounds) {round ->
            Card(modifier.fillMaxWidth()) {
                Column(modifier.fillMaxWidth()) {
                    Text(round.round.courseName)
                }
            }
        }
    }
}