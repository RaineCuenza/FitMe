package com.fitme.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fitme.app.FitMeApplication
import com.fitme.app.data.local.SettingsRepository
import com.fitme.app.data.remote.WeatherResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data class Ready(val weather: WeatherResponse, val city: String) : WeatherUiState
    data class Error(val description: String) : WeatherUiState
}

@OptIn(kotlinx.coroutines.ExperimentalCoroutinesApi::class)
class WeatherViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = (application as FitMeApplication).weatherRepository
    private val settings = SettingsRepository(application)
    private val refreshTrigger = MutableSharedFlow<Unit>(extraBufferCapacity = 1)

    val weather: StateFlow<WeatherUiState> = combine(
        settings.preferences.map { Triple(it.city, it.latitude, it.longitude) }.distinctUntilChanged(),
        refreshTrigger.onStart { emit(Unit) }
    ) { location, _ -> location }
        .flatMapLatest { location ->
            flow<WeatherUiState> {
                emit(WeatherUiState.Loading)
                try {
                    emit(WeatherUiState.Ready(repository.weather(location.second, location.third), location.first))
                } catch (e: CancellationException) { throw e }
                catch (e: Exception) {
                    emit(WeatherUiState.Error("Weather unavailable. Check your internet connection and try again."))
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), WeatherUiState.Loading)

    fun refresh() { refreshTrigger.tryEmit(Unit) }
}
