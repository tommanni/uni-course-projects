package com.example.harjoitus_6_8.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.example.harjoitus_6_8.R
import com.example.harjoitus_6_8.RecordsTopAppBar
import com.example.harjoitus_6_8.ui.common.BasicButton
import com.example.harjoitus_6_8.ui.common.BasicTextButton
import com.example.harjoitus_6_8.ui.common.EmailField
import com.example.harjoitus_6_8.ui.common.PasswordField
import com.example.harjoitus_6_8.ui.navigation.NavigationDestination

object LoginDestination : NavigationDestination {
    override val route: String = "login"
    override val titleRes = R.string.login_title
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LoginScreen(
    navigateToSignup: () -> Unit,
    navigateToHomeScreen: () -> Unit,
    viewModel: LoginViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState

    var wrongEmailOrPassword by remember { mutableStateOf(false)}

    Scaffold(
        topBar = {
            RecordsTopAppBar(
                title = stringResource(id = LoginDestination.titleRes),
                canNavigateBack = false
            )
        }
    ) { paddingValues ->
        LoginBody(
            uiState = uiState,
            wrongEmailOrPassword = wrongEmailOrPassword,
            setWrongEmailOrPassword = { wrongEmailOrPassword = true },
            onEmailChange = viewModel::onEmailChange,
            onPasswordChange = viewModel::onPasswordChange,
            onSignInClick = viewModel::onSignInClick,
            onForgotPasswordClick = viewModel::onForgotPasswordClick,
            navigateToSignup = navigateToSignup,
            navigateToHomeScreen = navigateToHomeScreen,
            modifier = Modifier.padding(paddingValues)
        )
    }


}

@Composable
fun LoginBody(
    uiState: LoginUiState,
    wrongEmailOrPassword: Boolean,
    setWrongEmailOrPassword: () -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSignInClick: (() -> Unit, () -> Unit) -> Unit,
    onForgotPasswordClick: () -> Unit,
    navigateToSignup: () -> Unit,
    navigateToHomeScreen: () -> Unit,
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

        if (wrongEmailOrPassword) {
            Text(text = "Wrong email or password. Try again.", color = Color.Red)
        }

        BasicButton(
            text = "Log in",
            action =  {
                onSignInClick(navigateToHomeScreen, setWrongEmailOrPassword)
            }
        )
        OutlinedButton(
            onClick = { navigateToSignup() },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = MaterialTheme.shapes.small
        ) {
            Text(text = "Sign up")
        }
        BasicTextButton(text = "Forgot password", action = { onForgotPasswordClick() })
    }
}