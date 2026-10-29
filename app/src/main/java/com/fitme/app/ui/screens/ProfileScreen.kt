package com.fitme.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitme.app.ui.viewmodel.SettingsViewModel

private val profileStyles = listOf("Casual", "Formal", "Sporty", "Minimalist", "Streetwear", "Smart Casual")
private val profileOccasions = listOf("School", "Casual Outing", "Formal Event", "Work", "Party", "Sports", "Special Events")

@Composable
fun ProfileScreen(viewModel: SettingsViewModel) {
    val prefs by viewModel.preferences.collectAsStateWithLifecycle()
    val cities by viewModel.cities.collectAsStateWithLifecycle()
    val cityError by viewModel.locationError.collectAsStateWithLifecycle()
    val searching by viewModel.searching.collectAsStateWithLifecycle()
    var cityQuery by remember { mutableStateOf("") }
    var showCityPicker by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(20.dp),
        verticalArrangement = Arrangement.spacedBy(17.dp)
    ) {
        Text("Profile & settings", style = MaterialTheme.typography.headlineMedium)
        Text("Personalize the way FitMe recommends outfits.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                    Icon(Icons.Outlined.LocationOn, contentDescription = null)
                    Text("Preferred location", style = MaterialTheme.typography.titleMedium)
                }
                Text(prefs.city)
                OutlinedButton(onClick = { showCityPicker = !showCityPicker }) { Text(if (showCityPicker) "Close search" else "Change city") }
                if (showCityPicker) {
                    OutlinedTextField(
                        value = cityQuery,
                        onValueChange = { cityQuery = it },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true,
                        label = { Text("City or town") },
                        placeholder = { Text("e.g., Calapan") }
                    )
                    Button(onClick = { viewModel.searchCities(cityQuery) }, enabled = cityQuery.trim().length >= 2 && !searching) {
                        Text(if (searching) "Searching…" else "Search locations")
                    }
                    if (cityError != null) Text(cityError.orEmpty(), color = MaterialTheme.colorScheme.error)
                    cities.forEach { city ->
                        TextButton(onClick = {
                            viewModel.chooseCity(city)
                            cityQuery = ""
                            showCityPicker = false
                        }, modifier = Modifier.fillMaxWidth()) { Text(city.displayName) }
                    }
                }
            }
        }
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text("Preferred style", style = MaterialTheme.typography.titleMedium)
                Text("Used in today's outfit recommendations.", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                profileStyles.chunked(2).forEach { options ->
                    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        options.forEach { option ->
                            FilterChip(selected = prefs.style == option, onClick = { viewModel.setStyle(option) }, label = { Text(option) })
                        }
                    }
                }
            }
        }
        Card(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Text("Typical occasion", style = MaterialTheme.typography.titleMedium)
                profileOccasions.chunked(2).forEach { options ->
                    Row(horizontalArrangement = Arrangement.spacedBy(7.dp)) {
                        options.forEach { option ->
                            FilterChip(selected = prefs.occasion == option, onClick = { viewModel.setOccasion(option) }, label = { Text(option) })
                        }
                    }
                }
            }
        }
        Card(Modifier.fillMaxWidth()) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(17.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(Modifier.weight(1f), horizontalArrangement = Arrangement.spacedBy(10.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Palette, contentDescription = null)
                    Column {
                        Text("Dark mode", style = MaterialTheme.typography.titleMedium)
                        Text("Saved automatically", style = MaterialTheme.typography.bodySmall)
                    }
                }
                Switch(checked = prefs.darkMode, onCheckedChange = viewModel::setDarkMode)
            }
        }
        OutlinedButton(
            onClick = { viewModel.setLoggedIn(false) },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Log out")
        }
        Text("FitMe · Offline wardrobe and outfit planner", style = MaterialTheme.typography.labelLarge)
        Text("Weather data: Open-Meteo (open-meteo.com).", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
    }
}
