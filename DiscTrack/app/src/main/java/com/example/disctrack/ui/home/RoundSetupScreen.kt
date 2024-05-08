package com.example.disctrack.ui.home

import android.content.res.Resources.Theme
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.disctrack.R
import com.example.disctrack.data.model.CourseListItem
import com.example.disctrack.ui.courses.CoursesViewModel
import com.example.disctrack.ui.navigation.NavigationDestination
import com.example.disctrack.ui.round.CreateCustomRoundDestination

object RoundSetupDestination: NavigationDestination {
    override val route: String = "setup"
    override val titleRes: Int = R.string.round_setup_title
}

/**
 * Round setup screen to setup a round of disc golf
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundSetupScreen(
    modifier: Modifier = Modifier,
    viewModel: CoursesViewModel = hiltViewModel(),
    navController: NavController
) {
    val uiState = viewModel.coursesUiState.collectAsState()
    var showingCourses by remember { mutableStateOf(true) }

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
        Column(
            modifier
                .fillMaxSize()
                .padding(it)
        ) {
            SelectCoursesButtons(
                setCoursesByButton = { button ->
                    when (button) {
                        "Nearby" -> {
                            showingCourses = true
                            viewModel.startLocationUpdates()
                            viewModel.setNearbyCourses()
                        }
                        "Recent" -> {
                            showingCourses = true
                            viewModel.stopLocationUpdates()
                            viewModel.setRecentCourses()
                        }
                        else -> {
                            viewModel.stopLocationUpdates()
                            showingCourses = false
                        }
                    }
                }
            )
            RoundSetupBody(
                showingCourses = showingCourses,
                courses = uiState.value.shownCourses,
                navigateToCustomRoundScreen = {
                    navController.navigate(CreateCustomRoundDestination.route)
                },
                navigateToRoundTrack = navController::navigate
            )
        }
    }
}

/**
 * Buttons to select which courses to show
 */
@Composable
fun SelectCoursesButtons(
    setCoursesByButton: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    // State for currently selected button
    var selectedButton by remember {
        mutableStateOf("Nearby")
    }

    // Set courses initially to nearby courses
    LaunchedEffect(Unit) {
        setCoursesByButton(selectedButton)
    }

    Column(modifier.fillMaxWidth()) {
        Row(
            Modifier.padding(dimensionResource(R.dimen.padding_medium))
        ) {
            Icon(Icons.Filled.LocationOn, "Location icon")
            Text(
                text = "Where are you playing?",
                fontSize = 20.sp
            )
        }
        Row(
            Modifier.padding(horizontal = dimensionResource(R.dimen.padding_medium))
        ) {
            // Create buttons to select which courses to show
            val buttons = listOf("Nearby", "Recent", "Custom")
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
                        onClick = { }, // Because button is already selected, do nothing
                        shape = shape,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = button
                        )
                    }
                } else {
                    OutlinedButton(
                        onClick = {
                            selectedButton = button
                            setCoursesByButton(button)
                                  },
                        shape = shape,
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxWidth()
                    ) {
                        Text(
                            text = button
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RoundSetupBody(
    showingCourses: Boolean,
    courses: List<CourseListItem>,
    navigateToCustomRoundScreen: () -> Unit,
    navigateToRoundTrack: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (showingCourses) {
        CoursesList(
            courses = courses,
            navigateToRoundTrack = navigateToRoundTrack
        )
    } else {
        CustomOption(navigateToCustomRoundScreen)
    }
}

@Composable
fun CoursesList(
    courses: List<CourseListItem>,
    navigateToRoundTrack: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(modifier.fillMaxSize()) {
        items(items = courses) {course ->
            course.fullName?.let {
                CourseListItem(
                    course,
                    navigateToRoundTrack
                )
            }
        }
    }
}

@Composable
fun CourseListItem(
    course: CourseListItem,
    navigateToRoundTrack: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_small))
            .clickable {
                navigateToRoundTrack(
                    "track/" + course.id + "/" + course.fullName
                )
            }
    ) {
        course.fullName?.let { Text(text = it) }
        course.city?.let { Text(text = it) }
    }
    Divider(
        modifier = Modifier
            .height(2.dp)
            .fillMaxWidth()
    )
}

/**
 * Custom option to make a custom scorecard
 */
@Composable
fun CustomOption(
    navigateToCustomRoundScreen: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium))
    ) {
        Text("Create a custom scorecard to track scores for a course not available in the database")
        Button(
            onClick = { navigateToCustomRoundScreen() },
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = dimensionResource(R.dimen.padding_medium))
        ) {
            Text(
                "Create a custom scorecard",
                fontWeight = FontWeight.Bold
            )
        }
    }
}

