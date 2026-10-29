package com.fitme.app.domain.recommendation

import com.fitme.app.data.local.ClothingItemEntity
import com.fitme.app.data.remote.WeatherResponse

data class OutfitSuggestion(val items: List<ClothingItemEntity>, val reason: String, val occasion: String)

data class WeatherSituation(val temperature: Double, val rainExpected: Boolean, val label: String) {
    val band: String get() = when {
        temperature >= 30 -> "Hot"
        temperature >= 24 -> "Warm"
        temperature >= 18 -> "Cool"
        else -> "Cold"
    }
}

object OutfitRecommender {
    fun describe(weather: WeatherResponse): WeatherSituation? {
        val current = weather.current ?: return null
        val rainProbability = weather.daily?.rainProbability?.firstOrNull() ?: 0
        val rainyCodes = setOf(51, 53, 55, 56, 57, 61, 63, 65, 66, 67, 80, 81, 82, 95, 96, 99)
        val rainy = current.precipitation > 0.1 || current.weatherCode in rainyCodes || rainProbability >= 55
        val label = when {
            current.weatherCode in rainyCodes -> "Rain or showers"
            current.weatherCode == 0 -> "Clear sky"
            current.weatherCode in 1..3 -> "Partly cloudy"
            current.weatherCode in 45..48 -> "Foggy"
            current.weatherCode in 71..77 -> "Snow"
            else -> "Variable conditions"
        }
        return WeatherSituation(current.temperature, rainy, label)
    }

    /** Deterministic recommendations using only outfits built from the bundled catalog. */
    fun suggest(
        catalog: List<ClothingItemEntity>,
        situation: WeatherSituation,
        occasion: String,
        style: String,
        limit: Int = 3
    ): List<OutfitSuggestion> {
        if (catalog.isEmpty()) return emptyList()
        fun score(item: ClothingItemEntity): Int {
            val weather = if (situation.band in item.weatherConditions) 6 else -2
            val occasionMatch = if (occasion in item.occasions) 5 else -3
            val styleMatch = if (style == item.style) 4 else 0
            val rainBonus = if (situation.rainExpected && "Rainy" in item.weatherConditions) 9 else 0
            val rainPenalty = if (situation.rainExpected && item.category in listOf("Outerwear", "Footwear") && "Rainy" !in item.weatherConditions) -8 else 0
            return weather + occasionMatch + styleMatch + rainBonus + rainPenalty
        }
        fun picks(category: String) = catalog.filter { it.category == category }.sortedWith(compareByDescending<ClothingItemEntity> { score(it) }.thenBy { it.id })

        val tops = picks("Tops")
        val bottoms = picks("Bottoms")
        val dresses = picks("Dresses")
        val shoes = picks("Footwear")
        val outerwear = picks("Outerwear")
        val accessories = picks("Accessories")
        if (shoes.isEmpty() || (tops.isEmpty() || bottoms.isEmpty()) && dresses.isEmpty()) return emptyList()
        val results = mutableListOf<OutfitSuggestion>()
        val signatures = mutableSetOf<String>()
        for (index in 0 until limit * 4) {
            val base = if (index % 2 == 1 && dresses.isNotEmpty()) {
                listOf(dresses[(index / 2) % dresses.size])
            } else if (tops.isNotEmpty() && bottoms.isNotEmpty()) {
                listOf(tops[(index / 2) % tops.size], bottoms[(index / 3) % bottoms.size])
            } else listOf(dresses[index % dresses.size])
            val chosen = base.toMutableList()
            chosen.add(shoes[index % shoes.size])
            if ((situation.rainExpected || situation.band in listOf("Cool", "Cold")) && outerwear.isNotEmpty()) {
                chosen.add(outerwear[index % outerwear.size])
            }
            if (!situation.rainExpected && accessories.isNotEmpty() && index % 2 == 0) {
                chosen.add(accessories[index % accessories.size])
            }
            val signature = chosen.map { it.id }.sorted().joinToString("-")
            if (!signatures.add(signature)) continue
            val advice = if (situation.rainExpected) {
                "Rain is possible, so consider water-resistant layers and suitable footwear."
            } else when (situation.band) {
                "Hot" -> "Lightweight pieces suit today's hot weather."
                "Warm" -> "Comfortable, breathable layers suit the warm forecast."
                "Cool" -> "Consider a light layer for cooler conditions."
                else -> "Warm layers can help with the cold weather."
            }
            results.add(OutfitSuggestion(chosen, "$advice Selected for $occasion with a $style style preference.", occasion))
            if (results.size == limit) break
        }
        return results
    }
}
