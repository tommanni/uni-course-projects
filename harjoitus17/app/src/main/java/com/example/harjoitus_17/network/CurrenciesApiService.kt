package com.example.harjoitus_17.network

import com.google.gson.annotations.SerializedName
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
interface CurrenciesApiService {
    @GET("EUR")
    suspend fun fetchCurrencies(): CurrencyApiResponse

    companion object {
        private const val BASE_URL =
            "https://v6.exchangerate-api.com/v6/dce33b3f1b66cf492caab74a/latest/"

        fun create(): CurrenciesApiService {
            val retrofit = Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
            return retrofit.create(CurrenciesApiService::class.java)
        }
    }
}

data class ConversionRates(
    @SerializedName("USD") val usd: Double,
    @SerializedName("JPY") val jpy: Double,
    @SerializedName("GBP") val gbp: Double
)

data class CurrencyApiResponse(
    @SerializedName("conversion_rates") val conversionRates: ConversionRates
)