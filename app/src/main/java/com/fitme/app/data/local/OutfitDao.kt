package com.fitme.app.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface OutfitDao {
    @Transaction
    @Query("SELECT * FROM outfits ORDER BY createdAt DESC, id DESC")
    fun observeAll(): Flow<List<OutfitWithItems>>

    @Transaction
    @Query("SELECT * FROM outfits WHERE id = :id LIMIT 1")
    suspend fun getById(id: Long): OutfitWithItems?

    @Insert
    suspend fun insertOutfit(outfit: OutfitEntity): Long

    @Update
    suspend fun updateOutfit(outfit: OutfitEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertItems(items: List<OutfitItemCrossRef>)

    @Query("DELETE FROM outfit_items WHERE outfitId = :id")
    suspend fun clearItems(id: Long)

    @Query("UPDATE outfits SET isFavorite = :favorite WHERE id = :id")
    suspend fun setFavorite(id: Long, favorite: Boolean)

    @Query("DELETE FROM outfits WHERE id = :id")
    suspend fun deleteOutfit(id: Long)

    @Query("SELECT * FROM outfit_plans ORDER BY plannedDate ASC, id DESC")
    fun observePlans(): Flow<List<OutfitPlanEntity>>

    @Query("DELETE FROM outfit_plans WHERE plannedDate = :date")
    suspend fun deletePlansForDate(date: String)

    @Insert
    suspend fun addPlan(plan: OutfitPlanEntity): Long

    @Query("DELETE FROM outfit_plans WHERE id = :planId")
    suspend fun deletePlan(planId: Long)
}
