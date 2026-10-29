package com.fitme.app.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ClothingDao {
    @Query("SELECT * FROM clothing_items ORDER BY id ASC")
    fun observeAll(): Flow<List<ClothingItemEntity>>

    @Query("SELECT * FROM clothing_items WHERE id = :id LIMIT 1")
    fun observeById(id: Long): Flow<ClothingItemEntity?>

    @Query("SELECT COUNT(*) FROM clothing_items")
    suspend fun count(): Int

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(items: List<ClothingItemEntity>)
}
