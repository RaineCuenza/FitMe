package com.fitme.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitme.app.ui.navigation.FitMeApp
import com.fitme.app.ui.theme.FitMeTheme
import com.fitme.app.ui.viewmodel.SettingsViewModel

class MainActivity : ComponentActivity() {
    private val settingsViewModel: SettingsViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            val darkMode by settingsViewModel.isDarkMode.collectAsStateWithLifecycle()
            FitMeTheme(darkTheme = darkMode) {
                FitMeApp(settingsViewModel = settingsViewModel)
            }
        }
    }
}
