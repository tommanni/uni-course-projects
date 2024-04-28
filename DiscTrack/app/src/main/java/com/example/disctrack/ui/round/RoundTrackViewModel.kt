package com.example.disctrack.ui.round

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.disctrack.data.database.entities.PlayedHole
import com.example.disctrack.data.database.entities.Round
import com.example.disctrack.data.model.Basket
import com.example.disctrack.data.model.Course
import com.example.disctrack.data.model.CourseResponse
import com.example.disctrack.data.model.PlayedRound
import com.example.disctrack.data.repository.CourseDbRepository
import com.example.disctrack.data.repository.CourseRepository
import com.example.disctrack.ui.utils.getCurrentDateFormatted
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.io.IOException
import javax.inject.Inject

private const val TAG = "RoundTrackViewModel"

/**
 * ViewModel for tracking a disc golf round
 */
@HiltViewModel(assistedFactory = RoundTrackViewModel.RoundTrackViewModelFactory::class)
class RoundTrackViewModel @AssistedInject constructor(
    @Assisted val courseId: String,
    private val courseRepository: CourseRepository,
    private val courseDbRepository: CourseDbRepository
): ViewModel() {

    // Provide courseId to viewModel with RoundTrackViewModelFactory
    @AssistedFactory
    interface RoundTrackViewModelFactory {
        fun create(courseId: String): RoundTrackViewModel
    }

    private val _roundUiState = MutableStateFlow(RoundUiState())
    val roundUiState: StateFlow<RoundUiState> = _roundUiState

    init {
        fetchCourse()
    }

    // Fetch course from api by course id
    private fun fetchCourse() {
        viewModelScope.launch {
            try {
                var courseResponse = courseRepository.getCourseById(courseId)
                // Add finish as a basket to course response to use as finish screen button
                val finish = Basket(number = "Finish")
                courseResponse = courseResponse.copy(baskets = courseResponse.baskets?.plus(finish))
                val basketCount = courseResponse.baskets?.size ?: 1
                val hasBasketApiData = basketCount != 1
                var courseTotalPar = 0
                if (hasBasketApiData) {
                    courseResponse.baskets?.forEach {
                        if (it.par != null) {
                            courseTotalPar += it.par.toInt()
                        }
                    }
                }
                _roundUiState.value = _roundUiState.value.copy(
                    course = courseResponse,
                    scores = MutableList(basketCount) { 0 },
                    basketCount = basketCount,
                    courseTotalPar = courseTotalPar,
                    hasBasketApiData = hasBasketApiData
                )

                setRoundScore()
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    // Set current basket being tracked
    fun setCurrentBasket(basketNum: Int) {
        if (_roundUiState.value.course.baskets != null) {
            _roundUiState.value = _roundUiState.value.copy(
                currentBasketIndex = basketNum
            )
        }
    }

    // decrement hole score on button press
    fun decrementHoleScore() {
        val scores = _roundUiState.value.scores.toMutableList()
        val index = _roundUiState.value.currentBasketIndex
        scores[index] -= 1
        _roundUiState.value = _roundUiState.value.copy(
            scores = scores,

        )
        setRoundScore()
    }

    // Increment hole score on button press
    fun incrementHoleScore() {
        val scores = _roundUiState.value.scores.toMutableList()
        val index = _roundUiState.value.currentBasketIndex
        scores[index] += 1
        _roundUiState.value = _roundUiState.value.copy(
            scores = scores
        )
        setRoundScore()
    }

    // Set hole score initially to 3 when user clicks increment or decrement button
    fun setHoleScoreToInitialScore() {
        val scores = _roundUiState.value.scores.toMutableList()
        val index = _roundUiState.value.currentBasketIndex
        scores[index] = 3
        _roundUiState.value = _roundUiState.value.copy(
            scores = scores
        )
        setRoundScore()
    }

    // set state with default baskets data according to user given basket count
    fun setBaskets(basketCount: Int) {
        val course = _roundUiState.value.course
        // Create basketCount baskets and finish with default par value
        val baskets = (1 .. basketCount).map {
            Basket(
                number = it.toString(),
                par = "3"
            )
        }.plus(Basket(number = "Finish"))

        val scores = MutableList(baskets.size) { 0 }

        val courseTotalPar = basketCount * 3

        _roundUiState.value = _roundUiState.value.copy(
            course = course.copy(baskets = baskets),
            basketCount = basketCount,
            courseTotalPar = courseTotalPar,
            scores = scores
        )

        setRoundScore()
    }

    // Saves the played round to database
    fun savePlayedRound() {
        val round = Round(
            courseId = _roundUiState.value.course.course.id!!,
            courseName = _roundUiState.value.course.course.fullName!!,
            courseLocation = _roundUiState.value.course.course.city ?: "",
            date = getCurrentDateFormatted()
        )
        viewModelScope.launch {
            val roundId = courseDbRepository.insertRound(round)
            savePlayedHoles(roundId)
        }
    }

    // Save played holes to database
    private fun savePlayedHoles(roundId: Long) {
        viewModelScope.launch {
            val baskets = _roundUiState.value.course.baskets?.take(
                _roundUiState.value.basketCount - 1
            )
            baskets?.forEachIndexed { index, basket ->
                val playedHole = PlayedHole(
                    roundId = roundId,
                    holeNumber = basket.number!!,
                    par = basket.par?.toInt()!!,
                    throws = _roundUiState.value.scores[index]
                )
                courseDbRepository.insertPlayedHole(playedHole)
            }
        }
    }

    // Calculates the current round score
    private fun setRoundScore() {
        var totalScore = 0
        _roundUiState.value.scores.forEachIndexed {index, score ->
            if (score != 0) {
                val par = _roundUiState.value.course.baskets?.get(index)?.par?.toInt()!!
                totalScore += score - par
            }
        }
        _roundUiState.value = _roundUiState.value.copy(roundScore = totalScore)
    }
}

/**
 * UI state to hold state for round tracking
 */
data class RoundUiState(
    val course: CourseResponse = CourseResponse(Course()),
    val scores: List<Int> = listOf(),
    val basketCount: Int = 0,
    val courseTotalPar: Int = 0,
    val roundScore: Int = 0,
    val currentBasketIndex: Int = 0,
    val hasBasketApiData: Boolean = true
)