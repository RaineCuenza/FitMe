package com.fitme.app.data.model

import com.fitme.app.data.local.CatalogSeed
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class CatalogFilterEngineTest {
    private val clothing = CatalogSeed.items

    @Test fun bundledCatalogHasFortyEightUniqueEntries() {
        assertEquals(48, clothing.size)
        assertEquals(48, clothing.map { it.id }.toSet().size)
        assertEquals(48, clothing.map { it.imageReference }.toSet().size)
    }

    @Test fun allExpectedCategoriesAreAvailable() {
        assertEquals(
            setOf("Tops", "Bottoms", "Dresses", "Outerwear", "Footwear", "Accessories"),
            clothing.map { it.category }.toSet()
        )
    }

    @Test fun searchIgnoresCaseAndSurroundingSpaces() {
        val results = CatalogFilterEngine.apply(clothing, CatalogFilters(search = "  WHITE "))
        assertTrue(results.isNotEmpty())
        assertTrue(results.all { it.name.contains("white", ignoreCase = true) })
    }

    @Test fun searchCountsAsAnActiveFilter() {
        val filters = CatalogFilters(search = " white ", category = "All")
        assertEquals(1, filters.activeCount)

        val combined = CatalogFilters(search = " white ", category = "Tops", color = "White")
        assertEquals(3, combined.activeCount)
    }

    @Test fun categoryFilterReturnsOnlySelectedCategory() {
        val results = CatalogFilterEngine.apply(clothing, CatalogFilters(category = "Dresses"))
        assertEquals(6, results.size)
        assertTrue(results.all { it.category == "Dresses" })
    }

    @Test fun combinedFiltersReturnMatchingItemsOnly() {
        val results = CatalogFilterEngine.apply(
            clothing,
            CatalogFilters(category = "Tops", style = "Minimalist", occasion = "School")
        )
        assertTrue(results.isNotEmpty())
        assertTrue(results.all { it.category == "Tops" && it.style == "Minimalist" && "School" in it.occasions })
    }

    @Test fun unmatchedFiltersReturnEmptyList() {
        assertTrue(CatalogFilterEngine.apply(clothing, CatalogFilters(search = "no-such-item-zzz")).isEmpty())
    }
}
