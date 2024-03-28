package com.example.harjoitus_17.ui.screen

import android.content.Context
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.harjoitus_17.network.CurrenciesApiService
import com.example.harjoitus_17.network.CurrencyApiResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.io.IOException
import kotlin.math.log

class EuroConverterViewModel(
    private val currenciesApiService: CurrenciesApiService = CurrenciesApiService.create()
) : ViewModel() {

    private val _uiState = MutableStateFlow(EuroConverterState())
    val uiState: StateFlow<EuroConverterState> = _uiState.asStateFlow()

    init {
        getCurrencies()
    }

    fun setCurrentEuros(euros: Double) {
        _uiState.value = _uiState.value.copy(currentEuros = euros)
    }

    fun calculateCurrencies() {
        val currentEuros = _uiState.value.currentEuros
        val currentUSD = _uiState.value.USD * currentEuros
        val currentJPY = _uiState.value.JPY * currentEuros
        val currentGBP = _uiState.value.GBP * currentEuros
        Log.d("calculateCurrencies", currentUSD.toString())
        _uiState.value = _uiState.value.copy(
            eurosAsUSD = currentUSD,
            eurosAsJPY = currentJPY,
            eurosAsGBP = currentGBP
        )
    }

    private fun getCurrencies() {
        viewModelScope.launch {
            val response = currenciesApiService.fetchCurrencies()
            Log.d(" ", response.toString())
            val usd = response.conversionRates.usd
            val jpy = response.conversionRates.jpy
            val gbp = response.conversionRates.gbp
            _uiState.value = _uiState.value.copy(USD = usd, JPY = jpy, GBP = gbp)
        }
    }
}

data class EuroConverterState(
    val currentEuros: Double = 0.0,
    val USD: Double = 0.0,
    val JPY: Double = 0.0,
    val GBP: Double = 0.0,
    val eurosAsUSD: Double = 0.0,
    val eurosAsJPY: Double = 0.0,
    val eurosAsGBP: Double = 0.0,
)