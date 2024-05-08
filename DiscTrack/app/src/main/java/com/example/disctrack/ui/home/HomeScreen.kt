package com.example.disctrack.ui.home

import android.util.Log
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTimeFilled
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CardElevation
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.disctrack.R
import com.example.disctrack.data.database.entities.Round
import com.example.disctrack.data.model.PlayedRound
import com.example.disctrack.ui.common.DiscTrackBottomAppBar
import com.example.disctrack.ui.navigation.NavigationDestination
import kotlin.math.roundToInt

object HomeDestination : NavigationDestination {
    override val route: String = "home"
    override val titleRes = R.string.app_name
}

/**
 * Home screen to view played rounds and start a round
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel(),
    navController: NavController
) {
    val homeUiState by viewModel.homeUiState.collectAsState()

    SideEffect {
        viewModel.getPlayedRounds()
    }

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
        if (homeUiState.playedRounds.isEmpty()) {
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxSize()
            ) {
                Text(
                    text = "No rounds played yet",
                    fontSize = 16.sp
                )
            }
        } else {
            HomeBody(
                playedRounds = homeUiState.playedRounds,
                modifier = modifier.padding(it)
            )
        }
    }
}

@Composable
fun HomeBody(
    playedRounds: List<PlayedRound>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(0.dp)
    ) {
        items(playedRounds) {round ->
            var expanded by remember { mutableStateOf(false) }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp)
                    .clickable { expanded = !expanded  },
                elevation = CardDefaults.elevatedCardElevation(5.dp)
            ) {
                Column(
                    modifier = Modifier
                        .animateContentSize(
                            animationSpec = spring(
                                dampingRatio = Spring.DampingRatioNoBouncy,
                                stiffness = Spring.StiffnessMedium
                            )
                        )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                round.round.courseName,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.AccessTimeFilled,
                                    contentDescription = "Time icon",
                                    Modifier.size(16.dp)
                                )
                                Text(
                                    text = round.round.date
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Icon(
                                    imageVector = Icons.Filled.LocationOn,
                                    contentDescription = "Location icon",
                                    Modifier.size(16.dp)
                                )
                                Text(
                                    text =
                                    if (round.round.courseLocation == "")
                                            "Finland"
                                    else
                                        round.round.courseLocation
                                )
                            }
                        }
                        IconButton(
                            onClick = { expanded = !expanded }
                        ) {
                            Icon(
                                imageVector =
                                if (expanded)
                                    Icons.Filled.ExpandLess
                                else
                                    Icons.Filled.ExpandMore,
                                contentDescription = "Expand icon"
                            )
                        }
                    }
                    if (expanded) {
                        RoundInfoColumn(round = round)
                    }
                }
            }
        }
    }
}

@Composable
fun RoundInfoColumn(
    round: PlayedRound,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .padding(8.dp)
    ) {
        val score = (round.baskets.sumOf { it.throws } - round.baskets.sumOf { it.par })
        val sign = if (score > 0) "+" else ""
        val birdies = round.baskets.filter { it.par - it.throws == 1 }.size
        Log.d("ROundinfo", " " + birdies + round.baskets.size)
        val birdiePercentage = (birdies.toFloat() / round.baskets.size * 100).roundToInt()
        Row {
            Text("Score: $sign$score")
            Spacer(Modifier.width(4.dp))
            Text("(" + round.baskets.sumOf { it.throws }.toString() + ")")
        }
        Row {
            Text(
                "Birdies: $birdiePercentage% ($birdies)"

            )
        }
        if (round.round.rating != 0) {
            Row {
                Text(
                    "Rating: ${round.round.rating}"
                )
            }
        }
    }
}