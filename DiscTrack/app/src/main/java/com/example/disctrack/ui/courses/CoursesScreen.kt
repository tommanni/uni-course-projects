package com.example.disctrack.ui.courses

import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Shapes
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.disctrack.R
import com.example.disctrack.data.model.CourseListItem
import com.example.disctrack.ui.navigation.NavigationDestination
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState

object CoursesDestination: NavigationDestination {
    override val route: String = "courses"
    override val titleRes: Int = R.string.courses_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DiscTrackTopAppBar(
    title: String,
    setShowingListView: () -> Unit,
    showingListView: Boolean,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        title = { Text(text = title) },
        actions = {
            IconButton(
                onClick = { setShowingListView() },
                modifier = modifier
                    .padding(end = dimensionResource(id = R.dimen.padding_small))
            ) {
                Text(
                    text =
                    if (showingListView)
                        stringResource(R.string.switch_view_map)
                    else
                        stringResource(R.string.switch_view_list),
                )
            }
        }
    )
}

@Composable
fun CoursesScreen(
    viewModel: CoursesViewModel = hiltViewModel()
) {
    val coursesUiState by viewModel.coursesUiState.collectAsState()
    var showingListView by rememberSaveable {
        mutableStateOf(true)
    }

    Scaffold(
        topBar = {
            DiscTrackTopAppBar(
                title = "Courses",
                setShowingListView = { showingListView = !showingListView },
                showingListView = showingListView
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            CourseSearchTextField(
                setListView = { showingListView = true },
                showingListView = showingListView,
                getCoursesByNameOrLocation = viewModel::searchCoursesByNameOrLocation
            )
            CoursesBody(
                courses = coursesUiState.shownCourses,
                showingListView = showingListView
            )
        }
    }
}

/**
 * Text field where you can search for courses by name or location
 */
@Composable
fun CourseSearchTextField(
    setListView: () -> Unit,
    showingListView: Boolean,
    getCoursesByNameOrLocation: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current

    var value by remember { mutableStateOf("") }

    // If user switches to map, un focus text field
    if (!showingListView) {
        focusManager.clearFocus(true)
    }

    OutlinedTextField(
        value = value,
        onValueChange = {
            value = it
            getCoursesByNameOrLocation(value)
                        },
        singleLine = true,
        label = { Text(text = stringResource(R.string.course_search_textfield_label)) },
        leadingIcon = {
            Icon(Icons.Filled.Search, stringResource(R.string.search_icon_description))
                      },
        shape = RoundedCornerShape(16.dp),
        keyboardActions = KeyboardActions(
            onDone =  {
                // Lose focus
                focusManager.clearFocus(true)
            }
        ),
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_small))
            .onFocusChanged { focusState ->
                if (focusState.isFocused && !showingListView) {
                    setListView()
                }
            }
    )
}

@Composable
fun CoursesBody(
    courses: List<CourseListItem>,
    showingListView: Boolean,
    modifier: Modifier = Modifier,
) {
    if (showingListView) {
        CourseList(
            courses
        )
    } else {
        CoursesMap()
    }
}

@Composable
fun CourseList(
    courses: List<CourseListItem>,
    modifier: Modifier = Modifier,
) {
    LazyColumn {
        items(items = courses) {course ->
            course.fullName?.let {
                CourseListItem(course)
            }
        }
    }
}

@Composable
fun CourseListItem(
    course: CourseListItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(dimensionResource(R.dimen.padding_small))
            .clickable {

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

@Composable
fun CoursesMap(
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(61.49911, 23.78712),
            12f
        )
    }

    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                    context,
                    R.raw.style_json
                ),
                latLngBoundsForCameraTarget = LatLngBounds(
                    LatLng(60.023696, 19.870411),
                    LatLng(70.037472, 30.487070)
                ),
                minZoomPreference = 5f,
            )
        )
    }

    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                myLocationButtonEnabled = true,
                zoomControlsEnabled = false,
                rotationGesturesEnabled = false,
            )
        )
    }

    Box(
        modifier = modifier.fillMaxSize()
    ) {
        GoogleMap(
            cameraPositionState = cameraPositionState,
            properties = mapProperties,
            uiSettings = mapUiSettings,
            modifier = modifier
        ) {
        }
    }
}
