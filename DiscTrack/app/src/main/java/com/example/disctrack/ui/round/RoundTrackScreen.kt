package com.example.disctrack.ui.round

import android.content.res.Resources.Theme
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardColors
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.disctrack.R
import com.example.disctrack.ui.navigation.NavigationDestination
import com.example.disctrack.ui.theme.md_theme_dark_onPrimary
import com.example.disctrack.ui.theme.md_theme_dark_onSurface
import com.example.disctrack.ui.theme.md_theme_dark_surface
import com.example.disctrack.ui.theme.md_theme_light_tertiary

object RoundTrackDestination: NavigationDestination {
    override val route: String = "track/{courseId}/{courseName}"
    override val titleRes: Int = R.string.round_track_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundTrackScreen(
    navController: NavController,
    courseId: String?,
    courseName: String?,
    modifier: Modifier = Modifier
) {
    val viewModel = hiltViewModel<
            RoundTrackViewModel,
            RoundTrackViewModel.RoundTrackViewModelFactory> { factory ->
        factory.create(courseId!!)
    }
    val uiState = viewModel.roundUiState.collectAsState()

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(courseName ?: "") },
                // TODO: ask user if he wants to leave
                navigationIcon = { IconButton(onClick = { navController.navigateUp() }) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, "Stop round icon")
                }}
            )
        },
        bottomBar = {
            if (true) {
                Button(
                    onClick = { /*TODO*/ },
                    shape = RoundedCornerShape(8.dp),
                    modifier = modifier
                        .fillMaxWidth()
                        .padding(12.dp)
                ) {
                    Text(
                        "Finish Round",
                        fontSize = 16.sp
                    )
                }
            } else {
                OutlinedButton(
                    onClick = { /*TODO*/ },
                    modifier.fillMaxWidth()
                ) {
                    Text(
                        "Finish Round",
                        fontSize = 16.sp
                    )
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = modifier
                .fillMaxWidth()
                .padding(paddingValues)
                .padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            if (uiState.value.course.baskets != null) {
                items(uiState.value.course.baskets!!) {basket ->
                    Card(
                        modifier = Modifier
                            .fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp, 0.dp, 12.dp, 0.dp),
                        border = BorderStroke(2.dp, Color.Black)
                    ) {
                        Row(
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text(
                                text = basket.number ?: "",
                                fontSize = 40.sp,
                                modifier = Modifier
                                    .padding(20.dp)
                            )
                            Row(
                                Modifier.padding(end = 16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                IconButton(onClick = { /*TODO*/ }) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color.DarkGray,
                                        contentColor = Color.Black ,
                                        border = BorderStroke(2.dp, Color.Black),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Filled.Remove,
                                            contentDescription = "Remove throws icon"
                                        )
                                    }
                                }
                                Text("3", fontSize = 30.sp)
                                IconButton(onClick = { /*TODO*/ }) {
                                    Surface(
                                        shape = CircleShape,
                                        color = Color.DarkGray,
                                        contentColor = Color.Black,
                                        border = BorderStroke(2.dp, Color.Black),
                                        modifier = Modifier.size(36.dp)
                                    ) {
                                        Icon(
                                            Icons.Filled.Add,
                                            contentDescription = "Add throws icon"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}