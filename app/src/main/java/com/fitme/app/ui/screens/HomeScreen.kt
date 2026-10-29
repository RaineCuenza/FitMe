package com.fitme.app.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitme.app.domain.recommendation.OutfitRecommender
import com.fitme.app.ui.components.OutfitPreview
import com.fitme.app.ui.components.TitleSection
import com.fitme.app.ui.viewmodel.OutfitViewModel
import com.fitme.app.ui.viewmodel.SettingsViewModel
import com.fitme.app.ui.viewmodel.WeatherUiState
import com.fitme.app.ui.viewmodel.WeatherViewModel
import java.util.Locale

@Composable
fun HomeScreen(
    weatherViewModel: WeatherViewModel,
    settingsViewModel: SettingsViewModel,
    outfitViewModel: OutfitViewModel,
    onExploreClick: () -> Unit,
    onBuilderClick: () -> Unit,
    onFavoritesClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    val weatherState by weatherViewModel.weather.collectAsStateWithLifecycle()
    val preferences by settingsViewModel.preferences.collectAsStateWithLifecycle()
    val catalog by outfitViewModel.catalog.collectAsStateWithLifecycle()
    val suggestions = remember(weatherState, catalog, preferences) {
        val ready = weatherState as? WeatherUiState.Ready
        val situation = ready?.let { OutfitRecommender.describe(it.weather) }
        if (situation != null) OutfitRecommender.suggest(catalog, situation, preferences.occasion, preferences.style)
        else emptyList()
    }

    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(horizontal = 20.dp, vertical = 20.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(9.dp)) {
            Icon(Icons.Outlined.Spa, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
            Text("FitMe", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
        Text("A little more you, every day.", style = MaterialTheme.typography.titleLarge)
        Text("Plan outfits that match your day, style, and the weather.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Icon(Icons.Outlined.Cloud, contentDescription = null)
                    Text("Today's weather", style = MaterialTheme.typography.titleMedium)
                }
                Text(preferences.city, style = MaterialTheme.typography.bodyMedium)
                when (val state = weatherState) {
                    WeatherUiState.Loading -> { CircularProgressIndicator(Modifier.size(25.dp)); Text("Fetching weather…") }
                    is WeatherUiState.Error -> {
                        Text(state.description)
                        Row {
                            TextButton(onClick = weatherViewModel::refresh) { Text("Retry") }
                            TextButton(onClick = onProfileClick) { Text("Change city") }
                        }
                    }
                    is WeatherUiState.Ready -> {
                        val current = state.weather.current!!
                        val situation = OutfitRecommender.describe(state.weather)
                        Text(String.format(Locale.US, "%.0f°C", current.temperature), style = MaterialTheme.typography.headlineLarge, fontWeight = FontWeight.Bold)
                        Text("${situation?.label.orEmpty()} • Humidity ${current.humidity}% • Rain ${current.precipitation} mm")
                        TextButton(onClick = weatherViewModel::refresh) { Text("Refresh forecast") }
                    }
                }
            }
        }
        TitleSection("Outfits for your day", "Based on ${preferences.style.lowercase()} style and ${preferences.occasion.lowercase()} plans")
        if (suggestions.isEmpty()) {
            Card(Modifier.fillMaxWidth()) {
                Column(Modifier.padding(18.dp), verticalArrangement = Arrangement.spacedBy(7.dp)) {
                    Text("Outfits will appear when weather and catalog are ready.")
                    Text("You can still browse clothes or build outfits offline.", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    Button(onClick = onExploreClick) { Text("Explore clothing") }
                }
            }
        } else {
            suggestions.forEachIndexed { index, suggestion ->
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(15.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
                        Text("Look ${index + 1} · ${suggestion.occasion}", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        OutfitPreview(suggestion.items)
                        Text(suggestion.reason, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        Button(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = { outfitViewModel.saveSuggestion(suggestion, onFavoritesClick) }
                        ) { Text("Save this outfit") }
                    }
                }
            }
        }
        val forecast = (weatherState as? WeatherUiState.Ready)?.weather?.daily
        if (forecast != null && forecast.time.isNotEmpty()) {
            TitleSection("7-day forecast")
            forecast.time.take(7).forEachIndexed { index, day ->
                if (index < forecast.maximum.size && index < forecast.minimum.size) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(day)
                        Text("${forecast.minimum[index].toInt()}° – ${forecast.maximum[index].toInt()}° · Rain ${forecast.rainProbability.getOrElse(index) { 0 }}%")
                    }
                    HorizontalDivider()
                }
            }
        }
        TitleSection("Your style starts here")
        Row(horizontalArrangement = Arrangement.spacedBy(10.dp), modifier = Modifier.fillMaxWidth()) {
            FilledTonalButton(onClick = onExploreClick, modifier = Modifier.weight(1f)) { Text("Explore") }
            Button(onClick = onBuilderClick, modifier = Modifier.weight(1f)) { Text("Build an outfit") }
        }
        Spacer(Modifier.height(12.dp))
    }
}
