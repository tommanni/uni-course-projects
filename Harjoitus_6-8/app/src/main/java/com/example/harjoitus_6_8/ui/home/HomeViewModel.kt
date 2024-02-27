package com.example.harjoitus_6_8.ui.home

import androidx.compose.runtime.collectAsState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.harjoitus_6_8.data.RecordRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import com.example.harjoitus_6_8.data.Record
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(private val recordRepository: RecordRepository) : ViewModel() {

    /**
     * Holds home ui state. The list of items are retrieved from [RecordRepository] and mapped to
     * [HomeUiState]
     */
    private val _homeUiState = MutableStateFlow(HomeUiState())
    val homeUiState: StateFlow<HomeUiState> = _homeUiState

    init {
        viewModelScope.launch {
            recordRepository.sortByDate()
                .collect {recordList ->
                    _homeUiState.value = HomeUiState(recordList)
                }
        }
    }

    fun sortByDate() {
        viewModelScope.launch {
            recordRepository.sortByDate()
                .collect {recordList ->
                    _homeUiState.value = HomeUiState(recordList)
                }
        }
    }

    fun sortByWeight() {
        viewModelScope.launch {
            recordRepository.sortByWeight()
                .collect {recordList ->
                    _homeUiState.value = HomeUiState(recordList)
                }
        }
    }

    fun sortByName() {
        viewModelScope.launch {
            recordRepository.sortByName()
                .collect {recordList ->
                    _homeUiState.value = HomeUiState(recordList)
                }
        }
    }
}

/**
 * Ui state for HomeScreen
 */
data class HomeUiState(var recordList: List<Record> = listOf())