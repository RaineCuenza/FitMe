package com.fitme.app.ui.screens

import android.app.DatePickerDialog
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.fitme.app.data.local.OutfitWithItems
import com.fitme.app.ui.components.OutfitPreview
import com.fitme.app.ui.viewmodel.OutfitViewModel
import java.time.LocalDate

@Composable
fun FavoritesScreen(
    viewModel: OutfitViewModel,
    onEdit: (Long) -> Unit,
    onReuse: (Long) -> Unit,
    onBuild: () -> Unit
) {
    val outfits by viewModel.allOutfits.collectAsStateWithLifecycle()
    val plans by viewModel.plannedOutfits.collectAsStateWithLifecycle()
    val message by viewModel.message.collectAsStateWithLifecycle()
    var filter by remember { mutableStateOf("All outfits") }
    var pendingDelete by remember { mutableStateOf<Long?>(null) }
    val filtered = if (filter == "Favorites") outfits.filter { it.outfit.isFavorite } else outfits

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(18.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text("My outfits", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
            Text("Save favorites, reuse looks, or plan an outfit for a date.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        item {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                listOf("All outfits", "Favorites", "Planned").forEach { option ->
                    FilterChip(selected = filter == option, onClick = { filter = option }, label = { Text(option) })
                }
            }
        }
        if (filter == "Planned") {
            if (plans.isEmpty()) item { EmptyOutfits("No planned outfits", "Schedule a saved outfit using the Plan button.", onBuild) }
            items(plans, key = { "p${it.id}" }) { plan ->
                val outfit = outfits.firstOrNull { it.outfit.id == plan.outfitId }
                Card(Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        Text(plan.plannedDate, style = MaterialTheme.typography.titleMedium)
                        Text(outfit?.outfit?.name ?: "Saved outfit")
                        if (outfit != null) OutfitPreview(outfit.items)
                        TextButton(onClick = { viewModel.deletePlan(plan.id) }) {
                            Icon(Icons.Outlined.DeleteOutline, contentDescription = null)
                            Text("Remove plan")
                        }
                    }
                }
            }
        } else {
            if (filtered.isEmpty()) item {
                EmptyOutfits(
                    if (filter == "Favorites") "No favorites yet" else "No saved outfits yet",
                    if (filter == "Favorites") "Tap the heart on a saved outfit to add it here." else "Create your first outfit in the Builder.",
                    onBuild
                )
            }
            items(filtered, key = { "o${it.outfit.id}" }) { outfit ->
                SavedOutfitCard(
                    outfit = outfit,
                    onFavorite = { viewModel.toggleFavorite(outfit) },
                    onEdit = { onEdit(outfit.outfit.id) },
                    onReuse = { onReuse(outfit.outfit.id) },
                    onDelete = { pendingDelete = outfit.outfit.id },
                    onPlan = { date -> viewModel.plan(outfit.outfit.id, date) }
                )
            }
        }
    }

    if (pendingDelete != null) {
        AlertDialog(
            onDismissRequest = { pendingDelete = null },
            title = { Text("Delete outfit?") },
            text = { Text("This will permanently delete the outfit and its planned dates.") },
            confirmButton = {
                TextButton(onClick = { pendingDelete?.let(viewModel::delete); pendingDelete = null }) { Text("Delete") }
            },
            dismissButton = { TextButton(onClick = { pendingDelete = null }) { Text("Cancel") } }
        )
    }
    if (message != null) {
        AlertDialog(
            onDismissRequest = viewModel::dismissMessage,
            title = { Text("FitMe") },
            text = { Text(message.orEmpty()) },
            confirmButton = { TextButton(onClick = viewModel::dismissMessage) { Text("OK") } }
        )
    }
}

@Composable
private fun EmptyOutfits(title: String, subtitle: String, onBuild: () -> Unit) {
    Card(Modifier.fillMaxWidth()) {
        Column(Modifier.padding(22.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Text(title, style = MaterialTheme.typography.titleLarge)
            Text(subtitle, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Button(onClick = onBuild) { Text("Open Outfit Builder") }
        }
    }
}

@Composable
private fun SavedOutfitCard(
    outfit: OutfitWithItems,
    onFavorite: () -> Unit,
    onEdit: () -> Unit,
    onReuse: () -> Unit,
    onDelete: () -> Unit,
    onPlan: (String) -> Unit
) {
    val context = LocalContext.current
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(9.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(Modifier.weight(1f)) {
                    Text(outfit.outfit.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                    Text(outfit.outfit.occasion, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                IconButton(onClick = onFavorite) {
                    Icon(
                        if (outfit.outfit.isFavorite) Icons.Outlined.Favorite else Icons.Outlined.FavoriteBorder,
                        tint = if (outfit.outfit.isFavorite) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
                        contentDescription = if (outfit.outfit.isFavorite) "Remove from favorites" else "Add to favorites"
                    )
                }
            }
            OutfitPreview(outfit.items)
            Row(horizontalArrangement = Arrangement.spacedBy(6.dp), modifier = Modifier.fillMaxWidth()) {
                OutlinedButton(onClick = onEdit, modifier = Modifier.weight(1f)) { Text("Edit") }
                OutlinedButton(onClick = onReuse, modifier = Modifier.weight(1f)) { Text("Reuse") }
                OutlinedButton(onClick = onDelete, modifier = Modifier.weight(1f)) { Text("Delete") }
            }
            FilledTonalButton(onClick = {
                val now = LocalDate.now()
                DatePickerDialog(context, { _, year, month, day ->
                    onPlan(LocalDate.of(year, month + 1, day).toString())
                }, now.year, now.monthValue - 1, now.dayOfMonth).show()
            }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Outlined.CalendarMonth, contentDescription = null, modifier = Modifier.size(18.dp))
                Spacer(Modifier.width(8.dp))
                Text("Plan this outfit")
            }
        }
    }
}
