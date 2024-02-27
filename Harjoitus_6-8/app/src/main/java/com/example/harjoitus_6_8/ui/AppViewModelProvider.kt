package com.example.harjoitus_6_8.ui

import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.harjoitus_6_8.RecordsApplication
import com.example.harjoitus_6_8.ui.home.HomeViewModel
import com.example.harjoitus_6_8.ui.record.RecordDetailsViewModel
import com.example.harjoitus_6_8.ui.record.RecordEditViewModel
import com.example.harjoitus_6_8.ui.record.RecordEntryViewModel

/**
 * Provides Factory to create instance of ViewModel for the entire Inventory app
 */
object AppViewModelProvider {
    val Factory = viewModelFactory {
        initializer {
            HomeViewModel(recordsApplication().container.recordRepository)
        }

        initializer {
            RecordEditViewModel(
                this.createSavedStateHandle(),
                recordsApplication().container.recordRepository
            )
        }
        initializer {
            RecordEntryViewModel(recordsApplication().container.recordRepository)
        }
        initializer {
            RecordDetailsViewModel(
                this.createSavedStateHandle(),
                recordsApplication().container.recordRepository
            )
        }
    }
}

fun CreationExtras.recordsApplication(): RecordsApplication =
    (this[AndroidViewModelFactory.APPLICATION_KEY] as RecordsApplication)