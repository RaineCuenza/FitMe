package com.fitme.app.data.repository

import androidx.room.withTransaction
import com.fitme.app.data.local.CatalogSeed
import com.fitme.app.data.local.ClothingDao
import com.fitme.app.data.local.ClothingItemEntity
import com.fitme.app.data.local.FitMeDatabase
import kotlinx.coroutines.flow.Flow

class CatalogRepository(
    private val database: FitMeDatabase,
    private val dao: ClothingDao = database.clothingDao()
) {
    val catalog: Flow<List<ClothingItemEntity>> = dao.observeAll()

    fun observeItem(id: Long): Flow<ClothingItemEntity?> = dao.observeById(id)

    /** Idempotent initialization; IGNORE ensures duplicate IDs are never created. */
    suspend fun seedIfNeeded() {
        database.withTransaction {
            if (dao.count() < CatalogSeed.items.size) {
                dao.insertAll(CatalogSeed.items)
            }
        }
    }
}
