package com.fitme.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/** An offline catalog item that can be combined into user-saved outfits. */
@Entity(tableName = "clothing_items")
data class ClothingItemEntity(
    @PrimaryKey val id: Long,
    val name: String,
    val category: String,
    val color: String,
    val style: String,
    /** Pipe-delimited tags; simple, reliable storage for this fixed catalog. */
    val occasionTags: String,
    val weatherTags: String,
    /** A stable local drawable key (e.g., clothing_001). */
    val imageReference: String
) {
    val occasions: List<String> get() = occasionTags.split('|').filter(String::isNotBlank)
    val weatherConditions: List<String> get() = weatherTags.split('|').filter(String::isNotBlank)
}
