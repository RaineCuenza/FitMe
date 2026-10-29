package com.fitme.app.domain.recommendation

import com.fitme.app.data.local.ClothingItemEntity
import com.fitme.app.data.remote.CurrentWeather
import com.fitme.app.data.remote.DailyWeather
import com.fitme.app.data.remote.WeatherResponse
import org.junit.Assert.*
import org.junit.Test

class OutfitRecommenderTest {
    private fun item(id: Long, category: String, tags: String) = ClothingItemEntity(
        id, "Item $id", category, "White", "Casual", "School|Casual Outing", tags, "clothing_001"
    )
    private val catalog = listOf(
        item(1, "Tops", "Hot|Warm"), item(2, "Bottoms", "Hot|Warm"),
        item(3, "Dresses", "Hot|Warm"), item(4, "Footwear", "Warm|Hot"),
        item(5, "Footwear", "Rainy|Cool"), item(6, "Outerwear", "Rainy|Cool")
    )

    @Test fun rainyForecastUsesProtectiveLayers() {
        val weather = WeatherResponse(CurrentWeather(25.0, 85, 2.0, 61), DailyWeather(rainProbability = listOf(90)))
        val situation = OutfitRecommender.describe(weather)!!
        assertTrue(situation.rainExpected)
        val suggestions = OutfitRecommender.suggest(catalog, situation, "School", "Casual")
        assertTrue(suggestions.isNotEmpty())
        assertTrue(suggestions.first().items.any { it.id == 6L })
        assertTrue(suggestions.first().items.any { it.id == 5L })
    }

    @Test fun everySuggestionContainsAValidBaseAndFootwear() {
        val weather = WeatherResponse(CurrentWeather(31.0, 60, 0.0, 0), DailyWeather(rainProbability = listOf(0)))
        val situation = OutfitRecommender.describe(weather)!!
        assertFalse(situation.rainExpected)
        OutfitRecommender.suggest(catalog, situation, "Casual Outing", "Casual").forEach { suggestion ->
            assertTrue(suggestion.items.any { it.category == "Footwear" })
            assertTrue(suggestion.items.any { it.category == "Dresses" } ||
                (suggestion.items.any { it.category == "Tops" } && suggestion.items.any { it.category == "Bottoms" }))
        }
    }
}
