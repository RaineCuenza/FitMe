package com.fitme.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fitme.app.FitMeApplication
import com.fitme.app.data.local.SettingsRepository
import com.fitme.app.data.local.UserPreferences
import com.fitme.app.data.remote.CityLocation
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {
    private val settings = SettingsRepository(application.applicationContext)
    private val weatherRepository = (application as FitMeApplication).weatherRepository

    val loaded = settings.preferences.map { true }.stateIn(viewModelScope, SharingStarted.Eagerly, false)
    val preferences = settings.preferences.stateIn(viewModelScope, SharingStarted.Eagerly, UserPreferences())
    val isDarkMode = settings.isDarkMode.stateIn(viewModelScope, SharingStarted.Eagerly, false)
    private val _cities = MutableStateFlow<List<CityLocation>>(emptyList())
    val cities: StateFlow<List<CityLocation>> = _cities.asStateFlow()
    private val _locationError = MutableStateFlow<String?>(null)
    val locationError: StateFlow<String?> = _locationError.asStateFlow()
    private val _searching = MutableStateFlow(false)
    val searching: StateFlow<Boolean> = _searching.asStateFlow()
    private var citySearchJob: kotlinx.coroutines.Job? = null

    fun searchCities(query: String) {
        citySearchJob?.cancel()
        _cities.value = emptyList()
        _locationError.value = null
        if (query.trim().length < 2) { _searching.value = false; return }
        citySearchJob = viewModelScope.launch {
            _searching.value = true
            try {
                _cities.value = weatherRepository.searchCities(query)
                if (_cities.value.isEmpty()) _locationError.value = "No matching cities found."
            } catch (e: Exception) {
                if (e is kotlinx.coroutines.CancellationException) throw e
                _locationError.value = "Unable to search cities. Check your connection."
            } finally { _searching.value = false }
        }
    }

    fun chooseCity(city: CityLocation) {
        citySearchJob?.cancel()
        _cities.value = emptyList()
        _locationError.value = null
        viewModelScope.launch { settings.setCity(city.displayName, city.latitude, city.longitude) }
    }

    fun setDarkMode(enabled: Boolean) { viewModelScope.launch { settings.setDarkMode(enabled) } }
    fun setStyles(styles: Set<String>) { viewModelScope.launch { settings.setStyles(styles) } }
    fun completeOnboarding() { viewModelScope.launch { settings.completeOnboarding() } }
    fun setLoggedIn(loggedIn: Boolean) { viewModelScope.launch { settings.setLoggedIn(loggedIn) } }
    fun setStyle(style: String) { viewModelScope.launch { settings.setStyle(style) } }
    fun setOccasion(occasion: String) { viewModelScope.launch { settings.setOccasion(occasion) } }
}
