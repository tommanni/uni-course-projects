package com.example.disctrack.ui.statistics

import android.graphics.Typeface
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Done
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import co.yml.charts.axis.AxisData
import co.yml.charts.axis.DataCategoryOptions
import co.yml.charts.common.model.Point
import co.yml.charts.ui.barchart.BarChart
import co.yml.charts.ui.barchart.models.BarChartData
import co.yml.charts.ui.barchart.models.BarChartType
import co.yml.charts.ui.barchart.models.BarData
import co.yml.charts.ui.barchart.models.BarStyle
import co.yml.charts.ui.barchart.models.SelectionHighlightData
import com.example.disctrack.R
import com.example.disctrack.ui.navigation.NavigationDestination

object StatisticsDestination : NavigationDestination {
    override val route: String = "statistics"
    override val titleRes: Int = R.string.statistics_title
}


/**
 * StatisticsScreen to view statistics for played rounds
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
    modifier: Modifier = Modifier,
    viewModel: StatisticsViewModel = hiltViewModel()
) {
    val uiState = viewModel.statisticsUiState.collectAsState()
    // Make sure that latest data is shown when screen is composed
    LaunchedEffect(Unit) {
        viewModel.getStatisticsData()
    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(title = { Text("Statistics") })
        }
    ) {
        StatisticsBody(
            uiState = uiState.value,
            viewModel = viewModel,
            modifier = modifier.padding(it)
        )
    }
}

@Composable
fun StatisticsBody(
    uiState: StatisticsUiState,
    viewModel: StatisticsViewModel,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .verticalScroll(scrollState),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = uiState.amountOfRoundsPlayed.toString())
                Text(text = "Rounds")
            }
            VerticalDivider(Modifier.height(30.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = uiState.amountOfHolesPlayed.toString())
                Text(text = "Holes")
            }
            VerticalDivider(Modifier.height(30.dp))
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1f)
            ) {
                Text(text = uiState.totalAmountOfThrows.toString())
                Text(text = "Throws")
            }
        }
        Spacer(Modifier.size(16.dp))
        Text(
            text = "${uiState.amountOfCoursesPlayed} courses played"
        )
        Text(
            text = "Most played ${uiState.mostPlayedCourse.courseName}" +
                " - ${uiState.mostPlayedCourse.timesPlayed}"
        )
        TimeRangeSelectButtonRow(
            getStatisticsForSelectedRounds = viewModel::getStatisticsForSelectedRounds,
            getStatisticsForSelectedYear = viewModel::getStatisticsForSelectedYear,
            getStatisticsForAllTime = viewModel::getStatisticsForAllTime,
            yearsPlayed = uiState.yearsPlayed,
            currentYear = uiState.currentYear
        )
        Text(
            text = "Birdie percentage ${uiState.birdiePercentage}%"
        )
        ParPerformanceStatsBarChart(
            parPerformanceStats = uiState.parPerformanceStats
        )
    }

}

/**
 * Buttons to select which time range to show statistics for
 */
@Composable
fun TimeRangeSelectButtonRow(
    getStatisticsForSelectedRounds: (Int) -> Unit,
    getStatisticsForAllTime: () -> Unit,
    getStatisticsForSelectedYear: (String) -> Unit,
    yearsPlayed: List<String>,
    currentYear: String,
    modifier: Modifier = Modifier
) {
    // State for currently selected button
    var selectedButton by remember {
        mutableStateOf("Last 10")
    }
    var showSelectYearAlert by remember {
        mutableStateOf(false)
    }


    Row(
        Modifier.padding(vertical = 16.dp)
    ) {
        // Create buttons to select which courses to show
        val buttons = listOf("Last 10", "Last 20", currentYear, "All")
        buttons.forEach { button ->
            val isSelected = button == selectedButton
            // Adjust shape of button based on order
            val shape = when (button) {
                buttons.first() -> RoundedCornerShape(8.dp, 0.dp, 0.dp, 8.dp)
                buttons.last() -> RoundedCornerShape(0.dp, 8.dp, 8.dp, 0.dp)
                else -> RoundedCornerShape(0.dp)
            }
            // If button is selected, highlight it by using filled button, else use outlined one
            if (isSelected) {
                Button(
                    onClick = {
                        if (button == buttons[2]) {
                            showSelectYearAlert = true
                        }
                    },
                    shape = shape,
                    modifier = Modifier
                        .weight(if (button == buttons[3]) 0.8f else 1f)
                ) {
                    Text(
                        text = button
                    )
                    if (button == buttons[2]) {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "DropDown icon"
                        )
                    }
                }
            } else {
                OutlinedButton(
                    onClick = {
                        selectedButton = button
                        when (button) {
                            buttons[0] -> getStatisticsForSelectedRounds(10)
                            buttons[1] -> getStatisticsForSelectedRounds(20)
                            buttons[2] -> showSelectYearAlert = true
                            buttons[3] -> getStatisticsForAllTime()
                        }
                    },
                    shape = shape,
                    modifier = Modifier
                        .weight(if (button == buttons[3]) 0.8f else 1f)
                ) {
                    Text(
                        text = button
                    )
                    if (button == buttons[2]) {
                        Icon(
                            imageVector = Icons.Filled.ArrowDropDown,
                            contentDescription = "DropDown icon",
                        )
                    }
                }
            }
        }
    }

    if (showSelectYearAlert) {
        SelectYearAlertDialog(
            currentYear = currentYear,
            setShowDialog = { showSelectYearAlert = false },
            getStatisticsForSelectedYear = getStatisticsForSelectedYear,
            yearsPlayed = yearsPlayed
        )
    }
}

/**
 * Shows available years to show statistics for
 */
@Composable
fun SelectYearAlertDialog(
    currentYear: String,
    setShowDialog: () -> Unit,
    getStatisticsForSelectedYear: (String) -> Unit,
    yearsPlayed: List<String>,
    modifier: Modifier = Modifier
) {
    AlertDialog(
        onDismissRequest = {
            setShowDialog()
        },
        confirmButton = {
            Button(
                onClick = {
                    setShowDialog()
                },
                modifier = Modifier
                    .fillMaxWidth(),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Done")
            }
        },
        title = {
            Text(
                text = "Select year",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Select year to show statistics for",
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(16.dp))
                yearsPlayed.forEach { year ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .clickable {
                                getStatisticsForSelectedYear(year)
                                setShowDialog()
                            }
                    ) {
                        Text(
                            text = year,
                            fontWeight = FontWeight.Bold
                        )
                        if (year == currentYear) {
                            Icon(
                                imageVector = Icons.Filled.Done,
                                contentDescription = "Done icon",
                                tint = Color.Green
                            )
                        }
                    }
                    HorizontalDivider()
                }

            }
        },
        shape = RoundedCornerShape(16.dp)
    )
}

@Composable
fun ParPerformanceStatsBarChart(
    parPerformanceStats: ParPerformanceStats,
    modifier: Modifier = Modifier
) {
    val dataCategoryOptions = DataCategoryOptions(isDataCategoryInYAxis = true)
    val labels = listOf("Ace", "Birdie", "Par", "Bogey", "Dbl bogey")
    // Create barchart data
    val barDataList = arrayListOf<BarData>()
    labels.forEachIndexed { index, label ->
        val barData = BarData(
            Point(
                when (label) {
                    "Ace" -> parPerformanceStats.aces.toFloat()
                    "Birdie" -> parPerformanceStats.birdies.toFloat()
                    "Par" -> parPerformanceStats.pars.toFloat()
                    "Bogey" -> parPerformanceStats.bogeys.toFloat()
                    else -> parPerformanceStats.dblBogeys.toFloat()
                             },
                index.toFloat()
            ),
            color = Color.Green,
            dataCategoryOptions = dataCategoryOptions,
            label = label,

        )
        barDataList.add(barData)
    }

    val yAxisData = AxisData.Builder()
        .axisStepSize(30.dp)
        .steps(barDataList.size - 1)
        .labelAndAxisLinePadding(40.dp)
        .axisOffset(20.dp)
        .setDataCategoryOptions(
            DataCategoryOptions(
                isDataCategoryInYAxis = true,
                isDataCategoryStartFromBottom = false
            )
        )
        .backgroundColor(Color.Transparent)
        .labelData { index -> barDataList[index].label }
        .axisLabelFontSize(16.sp)
        .labelAndAxisLinePadding(4.dp)
        .axisLabelColor(MaterialTheme.colorScheme.onBackground)
        .axisLineColor(MaterialTheme.colorScheme.onBackground)
        .build()

    val barChartData = BarChartData(
        chartData = barDataList,
        yAxisData = yAxisData,
        barStyle = BarStyle(
            isGradientEnabled = false,
            paddingBetweenBars = 20.dp,
            barWidth = 35.dp,
            selectionHighlightData = SelectionHighlightData(
                highlightBarColor = Color.White,
                highlightTextBackgroundColor = Color.Transparent,
                popUpLabel = { x, _ -> "${x.toInt()}" },
                barChartType = BarChartType.HORIZONTAL,
                highlightTextColor = Color.White,
                highlightTextOffset = (-20).dp,
                highlightTextTypeface = Typeface.DEFAULT_BOLD,
                highlightTextSize = 16.sp
            )
        ),
        showYAxis = true,
        showXAxis = false,
        horizontalExtraSpace = 20.dp,
        barChartType = BarChartType.HORIZONTAL,
        backgroundColor = Color.Transparent,
    )
    BarChart(
        modifier = Modifier
            .height(350.dp),
        barChartData = barChartData
    )
}