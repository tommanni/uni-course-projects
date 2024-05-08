package com.example.disctrack.ui.round

import android.util.Log
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Done
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.example.disctrack.R
import com.example.disctrack.data.model.Basket
import com.example.disctrack.ui.home.HomeDestination
import com.example.disctrack.ui.navigation.NavigationDestination
import com.example.disctrack.ui.theme.md_theme_dark_background
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

object RoundTrackDestination: NavigationDestination {
    override val route: String = "track/{courseId}/{courseName}"
    override val titleRes: Int = R.string.round_track_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoundTrackScreenTopAppBar(
    setShowDialog: () -> Unit,
    courseName: String?,
    modifier: Modifier = Modifier
) {
    CenterAlignedTopAppBar(
        title = { Text(courseName ?: "") },
        // TODO: ask user if he wants to leave
        navigationIcon = { IconButton(onClick = { setShowDialog() }) {
            Icon(Icons.AutoMirrored.Filled.ArrowBack, "Stop round icon")
        }}
    )
}

/**
 * Screen to track a disc golf round
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun RoundTrackScreen(
    navController: NavController,
    courseId: String?,
    courseName: String?,
    modifier: Modifier = Modifier
) {
    val coroutineScope = rememberCoroutineScope()

    Log.d("roundtrackscren", courseId + courseName)

    // Initialize with ViewModelFactory to pass courseId and courseName as parameter
    val viewModel = hiltViewModel<
            RoundTrackViewModel,
            RoundTrackViewModel.RoundTrackViewModelFactory> { factory ->
        factory.create(
            AssistedParams(
                courseId!!, courseName!!
            )
        )
    }
    
    val uiState = viewModel.roundUiState.collectAsState()

    // Tracks if alert dialog is shown when user tries to leave tracking screen
    var showLeaveDialog by remember { mutableStateOf(false)}
    var showNoBasketsDialog by remember { mutableStateOf(false) }

    // If no baskets available in api, show alert to ask for basket count
    if (uiState.value.basketCount == 1) {
        showNoBasketsDialog = true
    }

    // lazyListState to hold state for LazyRow
    val lazyListState = rememberLazyListState()

    // State for horizontal pager
    val pagerState = rememberPagerState(initialPage = 0) {
        uiState.value.course.baskets?.size ?: 0
    }

    // Shows leave alert dialog if user presses system back button
    BackHandler {
        if (!showLeaveDialog) {
            showLeaveDialog = true
        }
    }

    if (showLeaveDialog) {
        LeaveRoundTrackingAlert(
            navController = navController,
            setShowDialog = { showLeaveDialog = false }
        )
    }

    if (showNoBasketsDialog) {
        NoBasketsAlert(
            navController = navController,
            setBaskets = viewModel::setBaskets,
            setShowDialog = { showNoBasketsDialog = false }
        )
    }

    val currentBasketIndex = uiState.value.currentBasketIndex
    // Triggers when current basket index changes and scrolls to that page
    LaunchedEffect(currentBasketIndex) {
        pagerState.animateScrollToPage(currentBasketIndex)
        lazyListState.animateScrollToItem(currentBasketIndex)
    }

    // Triggers when user swipes between screens and sets current basket
    LaunchedEffect(pagerState.currentPage) {
        viewModel.setCurrentBasket(pagerState.currentPage)
    }

    Scaffold(
        topBar = {
            RoundTrackScreenTopAppBar(
            setShowDialog = { showLeaveDialog = true },
            courseName = courseName
            ) },
        bottomBar = {
            NavigateHolesBottomBar(
                uiState = uiState,
                setCurrentBasket = viewModel::setCurrentBasket,
                lazyListState = lazyListState,
                coroutineScope = coroutineScope,
                pagerState = pagerState
            )
        }
    ) { paddingValues ->
        val currentBasket = uiState.value.course.baskets?.get(uiState.value.currentBasketIndex)

        HorizontalPager(state = pagerState) {page ->
            when (currentBasket?.number) {
                "Finish" -> RoundFinishPage(
                    uiState = uiState,
                    savePlayedRound = viewModel::savePlayedRound,
                    navController = navController,
                    modifier = modifier.padding(paddingValues)
                )
                else -> CurrentBasketPage(
                    currentBasket = currentBasket,
                    holeScore = uiState.value.scores[page],
                    viewModel = viewModel,
                    modifier = modifier.padding(paddingValues)
                )
            }
        }
    }
}


@Composable
fun CurrentBasketPage(
    currentBasket: Basket?,
    holeScore: Int,
    viewModel: RoundTrackViewModel,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        HoleInfoRow(
            updateHoleParValue = viewModel::updateHoleParValue,
            currentBasket = currentBasket
        )
        Spacer(modifier = Modifier.size(16.dp))
        HoleScoreAdjustRow(
            holeScore = holeScore,
            viewModel = viewModel
        )
    }
}

@Composable
fun RoundFinishPage(
    uiState: State<RoundUiState>,
    savePlayedRound: (() -> Unit) -> Unit,
    navController: NavController,
    modifier: Modifier = Modifier
) {
    val roundDone = !uiState.value.scores.take(uiState.value.scores.size - 1).contains(0)
    val scrollState = rememberScrollState()
    
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(dimensionResource(R.dimen.padding_medium))
            .verticalScroll(scrollState),
    ) {
        Row(
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    text = "Final",
                    color = Color.LightGray
                )
                Spacer(modifier = Modifier.size(8.dp))
                Text(
                    text = "1 - ${uiState.value.course.baskets?.size!! - 1}",
                    fontSize = 24.sp
                )
            }
            Text(
                (if (uiState.value.roundScore > 0) "+" else "") +
                        "${if (uiState.value.roundScore == 0) "E" else uiState.value.roundScore}" +
                        " (${uiState.value.courseTotalPar})",
                fontSize = 24.sp
            )
        }

        Text(
            text = if (roundDone) "ALL SCORES ENTERED" else "SCORES ARE MISSING",
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = if (roundDone) Color.DarkGray else Color.LightGray,
            modifier = Modifier
                .padding(vertical = 16.dp)
                .fillMaxWidth()
                .background(
                    if (roundDone) Color.Green else Color.Red
                ),
            textAlign = TextAlign.Center,
        )

        Row(
            Modifier
                .fillMaxWidth(),
        ) {
            val baskets = uiState.value.course.baskets?.take(uiState.value.course.baskets!!.size - 1)
            val scores = uiState.value.scores.take(uiState.value.course.baskets!!.size - 1)
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Hole")
                baskets?.forEach { basket ->
                    HorizontalDivider(
                        thickness = 2.dp,
                        color = Color.LightGray
                    )
                    Text(basket.number ?: "0")
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Par")
                baskets?.forEach { basket ->
                    HorizontalDivider(
                        thickness = 2.dp,
                        color = Color.LightGray
                    )
                    Text(basket.par ?: "0")
                }
            }
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Score")
                scores.forEach { score ->
                    HorizontalDivider(
                        thickness = 2.dp,
                        color = Color.LightGray
                    )
                    Text(if (score == 0) "-" else score.toString())
                }
            }
        }

        Button(
            onClick = {
                savePlayedRound {
                    navController.popBackStack(
                        route = HomeDestination.route,
                        inclusive = false
                    )
                }
            },
            shape = RoundedCornerShape(8.dp),
            enabled = roundDone,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),

            ) {
            Text("Finish")
        }
    }
}

@Composable
fun HoleInfoRow(
    currentBasket: Basket?,
    updateHoleParValue: (String, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var showUpdateParAlert by remember { mutableStateOf(false) }

    Row(
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier.fillMaxWidth()
    ) {
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = "Hole",
                color = Color.LightGray
            )
            Text(
                text = currentBasket?.number ?: "",
                fontSize = 24.sp
            )
        }
        Row(verticalAlignment = Alignment.Bottom) {
            Text(
                text = currentBasket?.length ?: "",
                fontSize = 24.sp
            )
            Text(
                text = if (currentBasket?.length != null) "m" else "",
                color = Color.LightGray
            )
        }
        OutlinedButton(
            onClick = { showUpdateParAlert = true },
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(
                text = if (currentBasket?.par != null) "Par" else "",
                color = Color.LightGray
            )
            Text(
                text = currentBasket?.par ?: "",
                fontSize = 24.sp,
                color = Color.White
            )
        }
    }
    if (showUpdateParAlert) {
        UpdateHoleParAlertDialog(
            currentBasket = currentBasket,
            setShowDialog = { showUpdateParAlert = false },
            updateHoleParValue = updateHoleParValue
        )
    }
}

@Composable
fun UpdateHoleParAlertDialog(
    currentBasket: Basket?,
    updateHoleParValue: (String, String) -> Unit,
    setShowDialog: () -> Unit
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
                text = "Update par",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
        },
        text = {
            Column {
                Text(
                    text = "Update par for this round only",
                    fontSize = 16.sp
                )
                Spacer(Modifier.height(16.dp))
                (2..6).forEach { par ->
                    Row(
                        horizontalArrangement = Arrangement.SpaceBetween,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 16.dp)
                            .clickable {
                                updateHoleParValue(par.toString(), currentBasket?.number!!)
                                setShowDialog()
                            }
                    ) {
                        Text(
                            text = "Par $par",
                            fontWeight = FontWeight.Bold
                        )
                        if (par.toString() == currentBasket?.par!!) {
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
fun HoleScoreAdjustRow(
    holeScore: Int,
    viewModel: RoundTrackViewModel,
    modifier: Modifier = Modifier
) {
    Row(
        modifier.padding(end = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = {
                if (holeScore == 0) {
                    viewModel.setHoleScoreToInitialScore()
                } else if (holeScore > 1) {
                    viewModel.decrementHoleScore()
                }
            }
        ) {
            Surface(
                shape = CircleShape,
                color = Color.Green,
                contentColor = Color.Black ,
                border = BorderStroke(2.dp, Color.Black),
                modifier = Modifier.size(200.dp)
            ) {
                Icon(
                    imageVector = Icons.Filled.Remove,
                    contentDescription = "Remove throws icon"
                )
            }
        }
        Text(
            text = if (holeScore == 0) "-" else holeScore.toString(),
            fontSize = 60.sp,
            fontWeight = FontWeight.Bold
        )
        IconButton(
            onClick = {
                if (holeScore == 0) {
                    viewModel.setHoleScoreToInitialScore()
                } else if (holeScore < 20) {
                    viewModel.incrementHoleScore()
                }
            },
        ) {
            Surface(
                shape = CircleShape,
                color = Color.Green,
                contentColor = Color.Black,
                border = BorderStroke(2.dp, Color.Black),
                modifier = Modifier.size(200.dp)
            ) {
                Icon(
                    Icons.Filled.Add,
                    contentDescription = "Add throws icon"
                )
            }
        }
    }
}

/**
 * Bottom bar to navigate between holes for round
 */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun NavigateHolesBottomBar(
    lazyListState: LazyListState,
    pagerState: PagerState,
    uiState: State<RoundUiState>,
    setCurrentBasket: (Int) -> Unit,
    coroutineScope: CoroutineScope,
    modifier: Modifier = Modifier
) {


    LazyRow(
        modifier = Modifier.height(48.dp),
        verticalAlignment = Alignment.CenterVertically,
        state = lazyListState
    ) {
        item {
            Spacer(modifier = modifier.size(40.dp))
        }
        if (uiState.value.course.baskets != null) {
            itemsIndexed(uiState.value.course.baskets!!) { index, basket ->
                val selected = index == uiState.value.currentBasketIndex
                Button(
                    onClick = {
                        coroutineScope.launch {
                            setCurrentBasket(index)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(md_theme_dark_background),
                    modifier = Modifier
                        .width(100.dp),
                    shape = RoundedCornerShape(0.dp)
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = basket.number!!,
                            color = Color.White,
                            fontSize = 16.sp
                        )
                        if (selected) {
                            Divider(
                                color = Color.Green,
                                modifier = Modifier
                                    .height(4.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
        item {
            Spacer(modifier = modifier.size(40.dp))
        }
    }
}

/**
 * Alert shown when no baskets data is available for course
 */
@Composable
fun NoBasketsAlert(
    navController: NavController,
    setBaskets: (Int) -> Unit,
    setShowDialog: () -> Unit
) {
    var basketCountValue by remember { mutableStateOf("") }

    // State for currently selected button
    var selectedButton by remember { mutableStateOf("") }

    val focusManager = LocalFocusManager.current

    AlertDialog(
        onDismissRequest = { navController.navigateUp() },
        title = {
            Text("No baskets found for course")
        },
        text = {
            Column {
                Text(text = "Enter number of holes to track round")
                Spacer(modifier = Modifier.size(16.dp))
                Row(Modifier.fillMaxWidth()) {
                    // Create buttons to select which courses to show
                    val buttons = listOf("9", "18", "Other")
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
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = button
                                )
                            }
                        } else {
                            OutlinedButton(
                                onClick = {
                                    selectedButton = button
                                    val value = if (button != "Other") button else ""
                                    basketCountValue = value
                                },
                                shape = shape,
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = button
                                )
                            }
                        }
                    }
                }
                if (selectedButton == "Other") {
                    OutlinedTextField(
                        value = basketCountValue,
                        label = { Text("Holes") },
                        onValueChange = {
                            if (it.length <= 2) {
                                basketCountValue = it
                            }
                        },
                        shape = RoundedCornerShape(8.dp),
                        singleLine = true,
                        keyboardActions = KeyboardActions(
                            onDone = {
                                // Lose focus
                                focusManager.clearFocus(true)
                            }
                        ),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        visualTransformation = VisualTransformation.None,
                    )
                }
            }
        },
        confirmButton = {
            TextButton(onClick = {
                setBaskets(basketCountValue.toInt())
                setShowDialog()
            }
            ) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = {
                navController.navigateUp()
            }) {
                Text("Dismiss")
            }
        }
    )
}

/**
 * Alert to confirm user intends to leave screen
 */
@Composable
fun LeaveRoundTrackingAlert(
    navController: NavController,
    setShowDialog: () -> Unit
) {
    AlertDialog(
        onDismissRequest = { setShowDialog() },
        title = {
            Text("Leave round tracking?")
        },
        text = {
            Text("All round tracking data for the round will be lost.")
        },
        confirmButton = {
            TextButton(onClick = { navController.navigateUp() }) {
                Text("Confirm")
            }
        },
        dismissButton = {
            TextButton(onClick = { setShowDialog() }) {
                Text("Dismiss")
            }
        }
    )
}




