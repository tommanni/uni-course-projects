package com.example.disctrack.ui.statistics

import android.health.connect.datatypes.units.Percentage
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.ColumnInfo
import com.example.disctrack.data.model.PlayedRound
import com.example.disctrack.data.repository.CourseDbRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import javax.inject.Inject
import kotlin.math.roundToInt

@HiltViewModel
class StatisticsViewModel @Inject constructor(
    private val courseDbRepository: CourseDbRepository
) : ViewModel() {

    private var _statisticsUiState = MutableStateFlow(StatisticsUiState())
    val statisticsUiState: StateFlow<StatisticsUiState> = _statisticsUiState

    init {
        getStatisticsData()
    }

    fun getStatisticsForSelectedRounds(rounds: Int) {
        viewModelScope.launch {
            val stats = courseDbRepository.getParPerformanceStatisticsForSelectedRounds(rounds)
            _statisticsUiState.value = _statisticsUiState.value.copy(
                parPerformanceStats = stats
            )
        }
    }
    fun getStatisticsForSelectedYear(year: String) {
        viewModelScope.launch {
            val stats = courseDbRepository.getParPerformanceStatisticsForYear(year)
            _statisticsUiState.value = _statisticsUiState.value.copy(
                parPerformanceStats = stats,
                currentYear = year
            )
        }
    }
    fun getStatisticsForAllTime() {
        viewModelScope.launch {
            val stats = courseDbRepository.getParPerformanceStatistics()
            _statisticsUiState.value = _statisticsUiState.value.copy(
                parPerformanceStats = stats
            )
        }
    }

    // Initial statistics
    fun getStatisticsData() {
        viewModelScope.launch {
            _statisticsUiState.value = _statisticsUiState.value.copy(
                mostPlayedCourse = courseDbRepository.getMostPlayedCourse(),
                amountOfCoursesPlayed = courseDbRepository.getAmountOfCoursesPlayed(),
                amountOfRoundsPlayed = courseDbRepository.getAmountOfRoundsPlayed(),
                amountOfHolesPlayed = courseDbRepository.getAmountOfHolesPlayed(),
                totalAmountOfThrows = courseDbRepository.getTotalAmountOfThrows(),
                yearsPlayed = courseDbRepository.getYearsPlayed(),
                parPerformanceStats = courseDbRepository
                    .getParPerformanceStatisticsForSelectedRounds(10),
                currentYear = LocalDate.now().year.toString()
            )
            setBirdiePercentage()
        }
    }

    private fun setBirdiePercentage() {
        viewModelScope.launch {
            val holesPlayed = _statisticsUiState.value.amountOfHolesPlayed
            val birdies = courseDbRepository.getAmountOfBirdies()
            val birdiePercentage =
                if (holesPlayed > 0)
                        (birdies.toFloat() / holesPlayed * 100).roundToInt()
                else
                    0
            Log.d("getStats", "" + birdiePercentage + " " + holesPlayed +  " " + birdies)
            _statisticsUiState.value = _statisticsUiState.value.copy(
                birdiePercentage = birdiePercentage
            )
        }
    }
}

/**
 * Ui state to hold data for StatisticsScreen
 */
data class StatisticsUiState(
    val mostPlayedCourse: MostPlayedCourse = MostPlayedCourse("-", 0),
    val amountOfCoursesPlayed: Long = 0,
    val amountOfRoundsPlayed: Long = 0,
    val amountOfHolesPlayed: Long = 0,
    val totalAmountOfThrows: Long = 0,
    val yearsPlayed: List<String> = listOf(),
    val currentYear: String = "",
    val playerRating: Int = 0,
    val parPerformanceStats: ParPerformanceStats = ParPerformanceStats(),
    val birdiePercentage: Int = 0
    )

/**
 * Data class to represent most played course
 */
data class MostPlayedCourse(
    @ColumnInfo(name = "courseName") val courseName: String,
    @ColumnInfo(name = "timesPlayed") val timesPlayed: Long
)

/**
 * Par performance stats
 */
data class ParPerformanceStats(
    val aces: Int = 0,
    val birdies: Int = 0,
    val pars: Int = 0,
    val bogeys: Int = 0,
    val dblBogeys: Int = 0
)