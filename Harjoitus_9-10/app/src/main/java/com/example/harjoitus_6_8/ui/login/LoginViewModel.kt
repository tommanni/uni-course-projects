package com.example.harjoitus_6_8.ui.login

import android.util.Patterns
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.harjoitus_6_8.model.service.AccountService
import com.example.harjoitus_6_8.ui.common.isValidEmail
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import java.util.regex.Pattern
import javax.inject.Inject
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val accountService: AccountService
) : ViewModel()  {
    var uiState = mutableStateOf(LoginUiState())
        private set

    private val email
        get() = uiState.value.email
    private val password
        get() = uiState.value.password

    fun onEmailChange(newValue: String) {
        uiState.value = uiState.value.copy(email = newValue)
    }

    fun onPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(password = newValue)
    }

    fun onSignInClick(navigateToHomeScreen: () -> Unit, setWrongEmailOrPassword: () -> Unit) {
        if (!email.isValidEmail()) {
            setWrongEmailOrPassword()
            return
        }

        if (password.isBlank()) {
            setWrongEmailOrPassword()
            return
        }

        viewModelScope.launch {
            accountService.authenticate(email, password)

            if (accountService.hasUser) {
                navigateToHomeScreen()
            } else {
                setWrongEmailOrPassword()
            }
        }
    }

    fun onForgotPasswordClick() {
        if (!email.isValidEmail()) {
            return
        }

        viewModelScope.launch {
            accountService.sendRecoveryEmail(email)
        }
    }
}

data class LoginUiState(
    val email: String = "",
    val password: String = ""
)