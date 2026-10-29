package com.fitme.app.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Checkroom
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitme.app.ui.components.ClothingArtwork
import com.fitme.app.ui.components.OutfitPreview
import com.fitme.app.ui.viewmodel.OutfitViewModel

private val builderCategories = listOf("Tops", "Bottoms", "Dresses", "Outerwear", "Footwear", "Accessories")
private val builderOccasions = listOf("School", "Casual Outing", "Formal Event", "Work", "Party", "Sports", "Special Events")

@Composable
fun OutfitBuilderScreen(viewModel: OutfitViewModel, onSaved: () -> Unit) {
    val state by viewModel.builder.collectAsStateWithLifecycle()
    val catalog by viewModel.catalog.collectAsStateWithLifecycle()
    val chosen = state.selections.values.mapNotNull { id -> catalog.find { it.id == id } }

    Column(Modifier.fillMaxSize()) {
        LazyColumn(
            modifier = Modifier.weight(1f),
            contentPadding = PaddingValues(18.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(if (state.editingId == null) "Build your outfit" else "Edit outfit", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
                Text("Mix and match clothing from your offline catalog.", color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            item {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text("Your outfit preview", style = MaterialTheme.typography.titleMedium)
                        OutfitPreview(chosen)
                        Text("Select a dress or a top + bottom, and footwear.", style = MaterialTheme.typography.bodySmall)
                    }
                }
            }
            item {
                OutlinedTextField(
                    value = state.name,
                    onValueChange = viewModel::setName,
                    label = { Text("Outfit name") },
                    placeholder = { Text("e.g., Monday campus outfit") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true
                )
            }
            item {
                Text("Occasion", style = MaterialTheme.typography.titleMedium)
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp), contentPadding = PaddingValues(vertical = 5.dp)) {
                    items(builderOccasions) { occasion ->
                        FilterChip(selected = state.occasion == occasion, onClick = { viewModel.setOccasion(occasion) }, label = { Text(occasion) })
                    }
                }
            }
            builderCategories.forEach { category ->
                item(key = category) {
                    Column(verticalArrangement = Arrangement.spacedBy(7.dp)) {
                        Text(category, style = MaterialTheme.typography.titleLarge)
                        if (catalog.none { it.category == category }) {
                            Text("Loading $category…", color = MaterialTheme.colorScheme.onSurfaceVariant)
                        } else {
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                                items(catalog.filter { it.category == category }, key = { it.id }) { clothing ->
                                    val selected = state.selections[category] == clothing.id
                                    Card(
                                        onClick = { viewModel.select(clothing) },
                                        modifier = Modifier.width(126.dp),
                                        colors = CardDefaults.cardColors(containerColor = if (selected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface),
                                        border = if (selected) androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.primary) else null
                                    ) {
                                        Column(Modifier.padding(7.dp), verticalArrangement = Arrangement.spacedBy(5.dp)) {
                                            Image(
                                                painter = painterResource(ClothingArtwork.forReference(clothing.imageReference)),
                                                contentDescription = clothing.name,
                                                modifier = Modifier.fillMaxWidth().height(110.dp),
                                                contentScale = ContentScale.Fit
                                            )
                                            Text(clothing.name, maxLines = 2, minLines = 2, style = MaterialTheme.typography.labelSmall)
                                            if (selected) Text("Selected ✓", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
            if (state.error != null) item { Text(state.error.orEmpty(), color = MaterialTheme.colorScheme.error) }
        }
        Surface(shadowElevation = 6.dp, modifier = Modifier.fillMaxWidth()) {
            Button(
                onClick = { viewModel.save(onSaved) },
                enabled = viewModel.canSave() && !state.busy,
                modifier = Modifier.fillMaxWidth().padding(horizontal = 18.dp, vertical = 12.dp)
            ) {
                if (state.busy) CircularProgressIndicator(Modifier.size(17.dp), strokeWidth = 2.dp)
                else Icon(Icons.Outlined.Checkroom, contentDescription = null, modifier = Modifier.size(19.dp))
                Spacer(Modifier.width(8.dp))
                Text(if (state.editingId == null) "Save outfit" else "Save changes")
            }
        }
    }
}
