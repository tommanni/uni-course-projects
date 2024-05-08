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
    @Assisted private val assistedParams: AssistedParams,
    private val courseRepository: CourseRepository,
    private val courseDbRepository: CourseDbRepository
): ViewModel() {

    // Provide courseId to viewModel with RoundTrackViewModelFactory
    @AssistedFactory
    interface RoundTrackViewModelFactory {
        fun create(assistedParams: AssistedParams): RoundTrackViewModel
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
                var courseResponse = courseRepository.getCourseById(assistedParams.courseId)
                // Add finish as a basket to course response to use as finish screen button
                val finish = Basket(number = "Finish")
                courseResponse = courseResponse.copy(
                    baskets = courseResponse.baskets?.plus(finish)
                )
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

    private fun createCustomCourse() {
        _roundUiState.value = _roundUiState.value.copy(
            course = CourseResponse(
                course = Course(
                    id = assistedParams.courseId,
                    name = assistedParams.courseName,
                    fullName = assistedParams.courseName,
                    type = "1",
                    countryCode = "FI"
                )
            ),
            hasBasketApiData = true
        )
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
            scores = scores
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
    fun savePlayedRound(onSaveCompleted: () -> Unit) {
        val course = _roundUiState.value.course.course

        val ratingValue1 = course?.ratingValue1
        val ratingValue2 = course?.ratingValue2
        val ratingResult1= course?.ratingResult1
        val ratingResult2 = course?.ratingResult2

        val rating = if (
                ratingResult1 != null &&
                ratingResult2 != null &&
                ratingValue1 != null &&
                ratingValue2 != null
            ) {
            val result = _roundUiState.value.scores.sumOf { it }
            // Rating = (RatingValue2 - RatingValue1)*(Result - RatingResult1)/
            // (RatingResult2 - RatingResult1)+RatingValue1
            (ratingValue2.toFloat() - ratingValue1.toFloat()) *
                    (result.toFloat() - ratingResult1.toFloat())/
                    (ratingResult2.toFloat() - ratingResult1.toFloat()) +
                    ratingValue1.toFloat()
        } else {
            0
        }

        val round = Round(
            courseId = assistedParams.courseId,
            courseName = assistedParams.courseName,
            courseLocation = _roundUiState.value.course.course?.city ?: "",
            date = getCurrentDateFormatted(),
            rating = rating.toInt()
        )

        viewModelScope.launch {
            val roundId = courseDbRepository.insertRound(round)
            savePlayedHoles(roundId)
            onSaveCompleted()
        }
    }

    fun updateHoleParValue(par: String, holeNumber: String) {
        var oldPar = ""
        val baskets = _roundUiState.value.course.baskets?.map { basket ->
            if (basket.number == holeNumber) {
                oldPar = basket.par!!
                basket.copy(
                    par = par
                )
            } else {
                basket
            }
        }
        val newCourseTotalPar = _roundUiState.value.courseTotalPar + (par.toInt() - oldPar.toInt())
        _roundUiState.value = _roundUiState.value.copy(
            course = _roundUiState.value.course.copy(
                baskets = baskets
            ),
            courseTotalPar = newCourseTotalPar
        )
    }

    // Save played holes to database
    private suspend fun savePlayedHoles(roundId: Long) {
        viewModelScope.launch {
            val baskets = _roundUiState.value.course.baskets?.take(
                _roundUiState.value.basketCount
            )
            baskets?.forEachIndexed { index, basket ->
                if (basket.number != "Finish") {
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
    }

    // Calculates the current round score and sets it to ui state
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

/**
 * data class for injecting parameters to viewModel
 */
data class AssistedParams(
    val courseId: String,
    val courseName: String
)