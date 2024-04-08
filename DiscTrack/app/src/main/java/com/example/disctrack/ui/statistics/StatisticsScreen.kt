package com.example.disctrack.ui.statistics

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.example.disctrack.R
import com.example.disctrack.ui.navigation.NavigationDestination

object StatisticsDestination : NavigationDestination {
    override val route: String = "statistics"
    override val titleRes: Int = R.string.statistics_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Statistics") })
        }
    ) {
        StatisticsBody(modifier.padding(it))
    }
}

@Composable
fun StatisticsBody(
    modifier: Modifier = Modifier
) {

}