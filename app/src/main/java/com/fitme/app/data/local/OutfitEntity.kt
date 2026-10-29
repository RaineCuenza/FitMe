package com.fitme.app.data.local

import androidx.room.*

@Entity(tableName = "outfits")
data class OutfitEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val occasion: String,
    val createdAt: Long = System.currentTimeMillis(),
    val isFavorite: Boolean = false
)

@Entity(
    tableName = "outfit_items",
    primaryKeys = ["outfitId", "clothingItemId"],
    foreignKeys = [
        ForeignKey(entity = OutfitEntity::class, parentColumns = ["id"], childColumns = ["outfitId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(entity = ClothingItemEntity::class, parentColumns = ["id"], childColumns = ["clothingItemId"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index("outfitId"), Index("clothingItemId")]
)
data class OutfitItemCrossRef(val outfitId: Long, val clothingItemId: Long)

data class OutfitWithItems(
    @Embedded val outfit: OutfitEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = OutfitItemCrossRef::class,
            parentColumn = "outfitId",
            entityColumn = "clothingItemId"
        )
    )
    val items: List<ClothingItemEntity>
)

@Entity(
    tableName = "outfit_plans",
    foreignKeys = [ForeignKey(entity = OutfitEntity::class, parentColumns = ["id"], childColumns = ["outfitId"], onDelete = ForeignKey.CASCADE)],
    indices = [Index("outfitId")]
)
data class OutfitPlanEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val outfitId: Long,
    val plannedDate: String,
    val notes: String = ""
)
