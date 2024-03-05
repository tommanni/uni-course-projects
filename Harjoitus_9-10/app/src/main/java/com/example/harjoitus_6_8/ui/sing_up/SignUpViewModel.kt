package com.example.harjoitus_6_8.ui.sing_up

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.harjoitus_6_8.model.service.AccountService
import com.example.harjoitus_6_8.ui.common.isValidEmail
import com.example.harjoitus_6_8.ui.common.isValidPassword
import com.example.harjoitus_6_8.ui.common.passwordMatches
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SignInViewModel @Inject constructor(
    private val accountService: AccountService
) : ViewModel() {
    var uiState = mutableStateOf(SignInUiState())
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

    fun onRepeatPasswordChange(newValue: String) {
        uiState.value = uiState.value.copy(repeatPassword = newValue)
    }

    fun onSignInClick() {
        if (!email.isValidEmail()) {
            return
        }

        if (!password.isValidPassword()) {
            return
        }

        if (!password.passwordMatches(uiState.value.repeatPassword)) {
            return
        }

        viewModelScope.launch {
            accountService.createAccount(email, password)
        }
    }
}

data class SignInUiState(
    val email: String = "",
    val password: String = "",
    val repeatPassword: String = ""
)