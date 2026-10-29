package com.fitme.app.data.remote

import com.google.gson.annotations.SerializedName
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

data class CurrentWeather(
    @SerializedName("temperature_2m") val temperature: Double = 0.0,
    @SerializedName("relative_humidity_2m") val humidity: Int = 0,
    @SerializedName("precipitation") val precipitation: Double = 0.0,
    @SerializedName("weather_code") val weatherCode: Int = -1
)

data class DailyWeather(
    val time: List<String> = emptyList(),
    @SerializedName("temperature_2m_max") val maximum: List<Double> = emptyList(),
    @SerializedName("temperature_2m_min") val minimum: List<Double> = emptyList(),
    @SerializedName("precipitation_probability_max") val rainProbability: List<Int> = emptyList()
)

data class WeatherResponse(
    val current: CurrentWeather? = null,
    val daily: DailyWeather? = null
)

data class CityLocation(
    val id: Long = 0,
    val name: String = "",
    val latitude: Double = 0.0,
    val longitude: Double = 0.0,
    val country: String = "",
    val admin1: String? = null
) {
    val displayName: String get() = listOfNotNull(name, admin1, country).filter { it.isNotBlank() }.distinct().joinToString(", ")
}

data class GeocodingResponse(val results: List<CityLocation>? = null)

interface ForecastService {
    @GET("v1/forecast")
    suspend fun forecast(
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("current") current: String = "temperature_2m,relative_humidity_2m,precipitation,weather_code",
        @Query("daily") daily: String = "temperature_2m_max,temperature_2m_min,precipitation_probability_max",
        @Query("forecast_days") forecastDays: Int = 7,
        @Query("timezone") timezone: String = "auto"
    ): WeatherResponse
}

interface GeocodingService {
    @GET("v1/search")
    suspend fun search(
        @Query("name") name: String,
        @Query("count") count: Int = 8,
        @Query("language") language: String = "en",
        @Query("format") format: String = "json"
    ): GeocodingResponse
}

class WeatherRepository {
    private val client = OkHttpClient.Builder()
        .connectTimeout(15, TimeUnit.SECONDS)
        .readTimeout(20, TimeUnit.SECONDS)
        .build()

    private fun retrofit(base: String) = Retrofit.Builder()
        .baseUrl(base)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val forecastApi = retrofit("https://api.open-meteo.com/").create(ForecastService::class.java)
    private val geocodingApi = retrofit("https://geocoding-api.open-meteo.com/").create(GeocodingService::class.java)

    suspend fun weather(latitude: Double, longitude: Double): WeatherResponse {
        val result = forecastApi.forecast(latitude, longitude)
        require(result.current != null && result.daily != null) { "Weather data is not available for this location." }
        return result
    }

    suspend fun searchCities(query: String): List<CityLocation> {
        if (query.trim().length < 2) return emptyList()
        return geocodingApi.search(query.trim()).results.orEmpty()
    }
}
