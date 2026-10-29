package com.fitme.app.data.local

import android.content.Context
import androidx.datastore.preferences.core.*
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.fitMeDataStore by preferencesDataStore(name = "fitme_settings")

data class UserPreferences(
    val darkMode: Boolean = false,
    val city: String = "Calapan City, Philippines",
    val latitude: Double = 13.41,
    val longitude: Double = 121.18,
    val style: String = "Casual",
    val occasion: String = "Casual Outing",
    val styles: String = "Minimalist|Smart Casual",
    val onboardingCompleted: Boolean = false,
    val loggedIn: Boolean = false
)

class SettingsRepository(private val context: Context) {
    private val darkModeKey = booleanPreferencesKey("dark_mode")
    private val cityKey = stringPreferencesKey("city")
    private val latKey = doublePreferencesKey("latitude")
    private val lonKey = doublePreferencesKey("longitude")
    private val styleKey = stringPreferencesKey("style")
    private val occasionKey = stringPreferencesKey("occasion")
    private val stylesKey = stringPreferencesKey("styles")
    private val onboardingKey = booleanPreferencesKey("onboarding_complete")
    private val loggedInKey = booleanPreferencesKey("logged_in")

    val preferences: Flow<UserPreferences> = context.fitMeDataStore.data.map { settings ->
        UserPreferences(
            darkMode = settings[darkModeKey] ?: false,
            city = settings[cityKey] ?: "Calapan City, Philippines",
            latitude = settings[latKey] ?: 13.41,
            longitude = settings[lonKey] ?: 121.18,
            style = settings[styleKey] ?: "Casual",
            occasion = settings[occasionKey] ?: "Casual Outing",
            styles = settings[stylesKey] ?: "Minimalist|Smart Casual",
            onboardingCompleted = settings[onboardingKey] ?: false,
            loggedIn = settings[loggedInKey] ?: false
        )
    }

    val isDarkMode: Flow<Boolean> = preferences.map { it.darkMode }

    suspend fun setDarkMode(enabled: Boolean) { context.fitMeDataStore.edit { it[darkModeKey] = enabled } }
    suspend fun setStyle(style: String) { context.fitMeDataStore.edit { it[styleKey] = style; it[stylesKey] = style } }
    suspend fun setStyles(styles: Set<String>) { context.fitMeDataStore.edit { it[stylesKey] = styles.joinToString("|"); it[styleKey] = styles.firstOrNull() ?: "Casual" } }
    suspend fun completeOnboarding() { context.fitMeDataStore.edit { it[onboardingKey] = true } }
    suspend fun setLoggedIn(loggedIn: Boolean) { context.fitMeDataStore.edit { it[loggedInKey] = loggedIn } }
    suspend fun setOccasion(occasion: String) { context.fitMeDataStore.edit { it[occasionKey] = occasion } }
    suspend fun setCity(name: String, latitude: Double, longitude: Double) {
        context.fitMeDataStore.edit {
            it[cityKey] = name
            it[latKey] = latitude
            it[lonKey] = longitude
        }
    }
}
