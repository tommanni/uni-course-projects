package com.example.harjoitus_6_8.ui.sing_up

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.harjoitus_6_8.R
import com.example.harjoitus_6_8.RecordsTopAppBar
import com.example.harjoitus_6_8.ui.common.BasicButton
import com.example.harjoitus_6_8.ui.common.EmailField
import com.example.harjoitus_6_8.ui.common.PasswordField
import com.example.harjoitus_6_8.ui.common.RepeatPasswordField
import com.example.harjoitus_6_8.ui.navigation.NavigationDestination

object SignInDestination : NavigationDestination {
    override val route = "signUp"
    override val titleRes: Int = R.string.sign_up_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SignInScreen(
    navigateUp: () -> Unit,
    navigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SignInViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState

    Scaffold(
        topBar = {
            RecordsTopAppBar(
                title = stringResource(id = SignInDestination.titleRes),
                canNavigateBack = true,
                navigateUp = { navigateUp() }
            )
        }
    ) { paddingValues ->
        SignInBody(
            uiState = uiState,
            navigateBack = navigateBack,
            onEmailChange = viewModel::onEmailChange,
            onPasswordChange = viewModel::onPasswordChange,
            onRepeatPasswordChange = viewModel::onRepeatPasswordChange,
            onSignInClick = viewModel::onSignInClick,
            modifier = Modifier.padding(paddingValues)
        )
    }
}

@Composable
fun SignInBody(
    uiState: SignInUiState,
    navigateBack: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onRepeatPasswordChange: (String) -> Unit,
    onSignInClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        EmailField(value = uiState.email, onNewValue = onEmailChange)
        PasswordField(value = uiState.password, label = "Password", onNewValue = onPasswordChange)
        RepeatPasswordField(value = uiState.repeatPassword, label = "Repeat password", onNewValue = onRepeatPasswordChange)

        BasicButton(
            text = "Sign up",
            action = {
                onSignInClick()
                navigateBack()
            }
        )
    }
}