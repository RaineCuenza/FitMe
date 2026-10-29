package com.fitme.app.data.model

import com.fitme.app.data.local.ClothingItemEntity

data class CatalogFilters(
    val search: String = "",
    val category: String = "All",
    val color: String? = null,
    val style: String? = null,
    val occasion: String? = null
) {
    val activeCount: Int get() =
        (if (search.trim().isNotEmpty()) 1 else 0) +
            listOfNotNull(color, style, occasion).size +
            if (category == "All") 0 else 1
}

/** Pure filtering logic; tests do not require Android or network access. */
object CatalogFilterEngine {
    fun apply(items: List<ClothingItemEntity>, filters: CatalogFilters): List<ClothingItemEntity> {
        val search = filters.search.trim()
        return items.filter { item ->
            (search.isEmpty() || item.name.contains(search, ignoreCase = true)) &&
                (filters.category == "All" || item.category == filters.category) &&
                (filters.color == null || item.color == filters.color) &&
                (filters.style == null || item.style == filters.style) &&
                (filters.occasion == null || item.occasions.contains(filters.occasion))
        }
    }
}
