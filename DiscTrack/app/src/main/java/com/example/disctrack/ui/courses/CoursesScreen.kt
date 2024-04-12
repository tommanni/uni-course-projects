package com.example.disctrack.ui.courses

import android.location.Location
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.disctrack.R
import com.example.disctrack.data.model.CourseListItem
import com.example.disctrack.ui.navigation.NavigationDestination
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.maps.android.clustering.ClusterItem
import com.google.maps.android.clustering.algo.NonHierarchicalDistanceBasedAlgorithm
import com.google.maps.android.clustering.algo.NonHierarchicalViewBasedAlgorithm
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.MapsComposeExperimentalApi
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.clustering.Clustering
import com.google.maps.android.compose.clustering.rememberClusterManager
import com.google.maps.android.compose.clustering.rememberClusterRenderer
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
    startLocationUpdates: (Boolean) -> Unit,
    stopLocationUpdates: () -> Unit,
    hasLocationPermission: Boolean,
    modifier: Modifier = Modifier,
) {
    CenterAlignedTopAppBar(
        title = { Text(text = title) },
        actions = {
            IconButton(
                onClick = {
                    setShowingListView()
                    // If user is viewing map, start location updates else stop
                    if (!showingListView) {
                        stopLocationUpdates()
                    } else {
                        startLocationUpdates(hasLocationPermission)
                    }
                          },
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
    hasLocationPermission: Boolean,
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
                showingListView = showingListView,
                startLocationUpdates = viewModel::startLocationUpdates,
                stopLocationUpdates = viewModel::stopLocationUpdates,
                hasLocationPermission = hasLocationPermission
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
                courses = coursesUiState.courses,
                shownCourses = coursesUiState.shownCourses,
                deviceOrientation = coursesUiState.deviceOrientation,
                showingListView = showingListView,
                userLastKnownLocation = coursesUiState.userLastKnownLocation
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
    shownCourses: List<CourseListItem>,
    showingListView: Boolean,
    userLastKnownLocation: Location,
    deviceOrientation: Float,
    modifier: Modifier = Modifier,
) {
    if (showingListView) {
        CourseList(
            courses = shownCourses
        )
    } else {
        CoursesMap(
            userLastKnownLocation = userLastKnownLocation,
            courses = courses,
            deviceOrientation = deviceOrientation
        )
    }
}

@Composable
fun CourseList(
    courses: List<CourseListItem>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
    ) {
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
    userLastKnownLocation: Location,
    courses: List<CourseListItem>,
    deviceOrientation: Float,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    // Set camera position state to the user last known location
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            LatLng(userLastKnownLocation.latitude, userLastKnownLocation.longitude),
            12f
        )
    }

    // Set maps style, map camera bounds and minimum zoom preference
    val mapProperties by remember {
        mutableStateOf(
            MapProperties(
                mapStyleOptions = MapStyleOptions.loadRawResourceStyle(
                    context,
                    R.raw.style_json
                ),
                latLngBoundsForCameraTarget = LatLngBounds(
                    // TODO: Adjust based on zoom level
                    LatLng(60.023696, 19.870411),
                    LatLng(70.037472, 30.487070)
                ),
                minZoomPreference = 5f,
            )
        )
    }

    // disable location button, zoom controls and rotation gestures from map ui settings
    val mapUiSettings by remember {
        mutableStateOf(
            MapUiSettings(
                myLocationButtonEnabled = false,
                zoomControlsEnabled = false,
                rotationGesturesEnabled = false,
            )
        )
    }

    val currentPosition = LatLng(userLastKnownLocation.latitude, userLastKnownLocation.longitude)

    GoogleMap(
        cameraPositionState = cameraPositionState,
        properties = mapProperties,
        uiSettings = mapUiSettings,
        modifier = modifier.fillMaxSize()
        ) {
        val currentLocationIcon = BitmapDescriptorFactory.fromResource(
            R.drawable.current_location_2_64
        )
        /*TODO: Implement device orientation to location marker */
        Marker(
            state = MarkerState(currentPosition),
            contentDescription = "Current location marker",
            icon = currentLocationIcon,
            anchor = Offset(0.5f, 0.5f),
            rotation = deviceOrientation
        )

        // Custom clustering implementation
        CustomRendererClustering(courses)
    }
}

@OptIn(MapsComposeExperimentalApi::class)
@Composable
fun CustomRendererClustering(courses: List<CourseListItem>) {
    val configuration = LocalConfiguration.current
    val screenHeight = configuration.screenHeightDp
    val screenWidth = configuration.screenWidthDp
    val clusterManager = rememberClusterManager<CourseListItem>()


    // Here the clusterManager is being customized with a NonHierarchicalViewBasedAlgorithm
    // to speed up rendering of items on map
    clusterManager?.setAlgorithm(
        NonHierarchicalViewBasedAlgorithm(
            screenWidth,
            screenHeight
        )
    )

    // Renderer to handle rendering of clustered markers on the map
    val renderer = rememberClusterRenderer(
        clusterContent = null,
        clusterItemContent = {
            Icon(
                imageVector = Icons.Filled.LocationOn,
                contentDescription = "Course location marker",
                modifier = Modifier.size(32.dp)
            )
        },
        clusterManager = clusterManager
    )

    // Ensure that clusterManager's renderer is correctly set
    SideEffect {
        if (clusterManager?.renderer != renderer) {
            clusterManager?.renderer = renderer ?: return@SideEffect
        }
    }

    if (clusterManager != null) {
        Clustering(
            items = courses,
            clusterManager = clusterManager
        )
    }
}