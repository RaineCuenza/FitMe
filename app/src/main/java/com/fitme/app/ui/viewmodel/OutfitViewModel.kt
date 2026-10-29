package com.fitme.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fitme.app.FitMeApplication
import com.fitme.app.data.local.ClothingItemEntity
import com.fitme.app.data.local.OutfitWithItems
import com.fitme.app.domain.recommendation.OutfitSuggestion
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class BuilderState(
    val editingId: Long? = null,
    val name: String = "",
    val occasion: String = "Casual Outing",
    val selections: Map<String, Long> = emptyMap(),
    val busy: Boolean = false,
    val error: String? = null
)

class OutfitViewModel(application: Application) : AndroidViewModel(application) {
    private val repo = (application as FitMeApplication).outfitRepository
    private val catalogRepo = (application as FitMeApplication).catalogRepository
    val allOutfits: StateFlow<List<OutfitWithItems>> = repo.outfits.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val plannedOutfits = repo.plans.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    val catalog = catalogRepo.catalog.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
    private val _builder = MutableStateFlow(BuilderState())
    val builder: StateFlow<BuilderState> = _builder.asStateFlow()
    private val _message = MutableStateFlow<String?>(null)
    val message: StateFlow<String?> = _message.asStateFlow()

    fun startNew(occasion: String = "Casual Outing") { _builder.value = BuilderState(occasion = occasion) }
    fun startEdit(id: Long) {
        viewModelScope.launch {
            val outfit = repo.find(id)
            _builder.value = if (outfit != null) {
                BuilderState(
                    editingId = id,
                    name = outfit.outfit.name,
                    occasion = outfit.outfit.occasion,
                    selections = outfit.items.associate { it.category to it.id }
                )
            } else BuilderState(error = "Outfit not found.")
        }
    }
    fun startReuse(id: Long) {
        viewModelScope.launch {
            val outfit = repo.find(id)
            _builder.value = if (outfit != null) BuilderState(
                name = "${outfit.outfit.name} (copy)",
                occasion = outfit.outfit.occasion,
                selections = outfit.items.associate { it.category to it.id }
            ) else BuilderState(error = "Outfit not found.")
        }
    }
    fun saveSuggestion(suggestion: OutfitSuggestion, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                val id = repo.save(null, "Weather-ready outfit", suggestion.occasion, suggestion.items.map { it.id })
                repo.setFavorite(id, true)
                onSuccess()
            } catch (e: Exception) {
                _message.value = e.message ?: "Unable to save recommendation."
            }
        }
    }
    fun clearBuilder(occasion: String = "Casual Outing") { _builder.value = BuilderState(occasion = occasion) }
    fun removeCategory(category: String) {
        _builder.value = _builder.value.copy(selections = _builder.value.selections - category, error = null)
    }
    fun saveFromSheet(favorite: Boolean, onSuccess: (Long) -> Unit) {
        if (!canSave() || _builder.value.busy) {
            _builder.value = _builder.value.copy(error = "Name your outfit and select a dress, or a top + bottom, plus footwear.")
            return
        }
        val state = _builder.value
        _builder.value = state.copy(busy = true, error = null)
        viewModelScope.launch {
            try {
                val id = repo.save(state.editingId, state.name, state.occasion, state.selections.values.toList())
                repo.setFavorite(id, favorite)
                _builder.value = BuilderState()
                onSuccess(id)
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _builder.value = _builder.value.copy(busy = false, error = e.message ?: "Unable to save outfit.")
            }
        }
    }
    fun setName(value: String) { _builder.value = _builder.value.copy(name = value, error = null) }
    fun setOccasion(value: String) { _builder.value = _builder.value.copy(occasion = value) }
    fun select(item: ClothingItemEntity) {
        val selections = _builder.value.selections.toMutableMap()
        if (item.category == "Dresses") { selections.remove("Tops"); selections.remove("Bottoms") }
        if (item.category == "Tops" || item.category == "Bottoms") selections.remove("Dresses")
        if (selections[item.category] == item.id) selections.remove(item.category) else selections[item.category] = item.id
        _builder.value = _builder.value.copy(selections = selections, error = null)
    }
    fun canSave(): Boolean {
        val values = _builder.value.selections
        return _builder.value.name.isNotBlank() && values.containsKey("Footwear") &&
            (values.containsKey("Dresses") || (values.containsKey("Tops") && values.containsKey("Bottoms")))
    }
    fun save(onSuccess: () -> Unit) {
        if (!canSave() || _builder.value.busy) {
            _builder.value = _builder.value.copy(error = "Enter a name and select a dress or a top and bottom, plus footwear.")
            return
        }
        val state = _builder.value
        _builder.value = state.copy(busy = true, error = null)
        viewModelScope.launch {
            try {
                repo.save(state.editingId, state.name, state.occasion, state.selections.values.toList())
                _builder.value = BuilderState()
                onSuccess()
            } catch (e: Exception) {
                if (e is CancellationException) throw e
                _builder.value = _builder.value.copy(busy = false, error = e.message ?: "Unable to save the outfit.")
            }
        }
    }
    fun toggleFavorite(outfit: OutfitWithItems) = viewModelScope.launch {
        try { repo.setFavorite(outfit.outfit.id, !outfit.outfit.isFavorite) }
        catch (e: Exception) { _message.value = e.message ?: "Unable to update favorite." }
    }
    fun delete(id: Long) = viewModelScope.launch {
        try { repo.delete(id) }
        catch (e: Exception) { _message.value = e.message ?: "Unable to delete outfit." }
    }
    fun plan(id: Long, isoDate: String) = viewModelScope.launch {
        try { repo.plan(id, isoDate); _message.value = "Outfit scheduled for $isoDate" }
        catch (e: Exception) { _message.value = e.message ?: "Unable to schedule outfit." }
    }
    fun deletePlan(id: Long) = viewModelScope.launch {
        try { repo.removePlan(id) }
        catch (e: Exception) { _message.value = e.message ?: "Unable to remove plan." }
    }
    fun dismissMessage() { _message.value = null }
}
