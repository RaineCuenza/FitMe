package com.fitme.app.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.fitme.app.FitMeApplication
import com.fitme.app.data.local.ClothingItemEntity
import com.fitme.app.data.model.CatalogFilterEngine
import com.fitme.app.data.model.CatalogFilters
import com.fitme.app.data.repository.CatalogRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

private sealed interface Initialization {
    data object Loading : Initialization
    data object Ready : Initialization
    data class Failed(val description: String) : Initialization
}

data class CatalogUiState(
    val isLoading: Boolean = true,
    val error: String? = null,
    val items: List<ClothingItemEntity> = emptyList(),
    val totalCount: Int = 0,
    val filters: CatalogFilters = CatalogFilters(),
    val colors: List<String> = emptyList(),
    val styles: List<String> = emptyList(),
    val occasions: List<String> = emptyList()
)

class CatalogViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: CatalogRepository =
        (application as FitMeApplication).catalogRepository

    private val search = MutableStateFlow("")
    private val category = MutableStateFlow("All")
    private val color = MutableStateFlow<String?>(null)
    private val style = MutableStateFlow<String?>(null)
    private val occasion = MutableStateFlow<String?>(null)
    private val initialization = MutableStateFlow<Initialization>(Initialization.Loading)

    private val filters: Flow<CatalogFilters> = combine(
        search, category, color, style, occasion
    ) { query, currentCategory, currentColor, currentStyle, currentOccasion ->
        CatalogFilters(query, currentCategory, currentColor, currentStyle, currentOccasion)
    }

    val uiState: StateFlow<CatalogUiState> = combine(
        repository.catalog, filters, initialization
    ) { items, currentFilters, status ->
        CatalogUiState(
            isLoading = status == Initialization.Loading,
            error = (status as? Initialization.Failed)?.description,
            items = CatalogFilterEngine.apply(items, currentFilters),
            totalCount = items.size,
            filters = currentFilters,
            colors = items.map { it.color }.distinct().sorted(),
            styles = items.map { it.style }.distinct().sorted(),
            occasions = items.flatMap { it.occasions }.distinct().sorted()
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), CatalogUiState())

    private var loadingStarted = false

    init { loadCatalog() }

    fun loadCatalog() {
        if (initialization.value == Initialization.Loading && loadingStarted) return
        initialization.value = Initialization.Loading
        loadingStarted = true
        viewModelScope.launch {
            try {
                repository.seedIfNeeded()
                initialization.value = Initialization.Ready
            } catch (error: Exception) {
                initialization.value = Initialization.Failed(
                    error.localizedMessage ?: "Unable to load the clothing catalog."
                )
            } finally {
                loadingStarted = false
            }
        }
    }

    fun setSearch(value: String) { search.value = value }
    fun setCategory(value: String) { category.value = value }
    fun setColor(value: String?) { color.value = value }
    fun setStyle(value: String?) { style.value = value }
    fun setOccasion(value: String?) { occasion.value = value }

    fun clearFilters() {
        search.value = ""
        category.value = "All"
        color.value = null
        style.value = null
        occasion.value = null
    }

    fun observeItem(id: Long): Flow<ClothingItemEntity?> = repository.observeItem(id)
}
