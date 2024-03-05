package com.example.harjoitus_6_8.ui.home

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.harjoitus_6_8.model.Record
import com.example.harjoitus_6_8.model.service.AccountService
import com.example.harjoitus_6_8.model.service.StorageService
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val storageService: StorageService,
    private val accountService: AccountService
) : ViewModel() {

    /**
     * Holds home ui state. The list of items are retrieved from [RecordRepository] and mapped to
     * [HomeUiState]
     */
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState

    init {
        sortByDate()
    }

    fun sortByDate() {
        viewModelScope.launch {
            storageService.sortByDate().collect {
                _homeUiState.value = HomeUiState(it)
            }
        }
    }

    fun sortByWeight() {
        viewModelScope.launch {
            storageService.sortByWeight().collect {
                _homeUiState.value = HomeUiState(it)
            }
        }
    }

    fun sortByName() {
        viewModelScope.launch {
            storageService.sortByName().collect {
                _homeUiState.value = HomeUiState(it)
            }
        }
    }

    fun onLogOut() {
        viewModelScope.launch {
            accountService.signOut()
        }
    }
}

/**
 * Ui state for HomeScreen
 */
data class HomeUiState(var recordList: List<Record> = listOf())