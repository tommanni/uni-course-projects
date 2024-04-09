package com.example.disctrack

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * View model to handle logic in the main activity
 */
@HiltViewModel
class MainViewModel @Inject constructor(

): ViewModel() {
    var hasLocationPermission by mutableStateOf(false)
}