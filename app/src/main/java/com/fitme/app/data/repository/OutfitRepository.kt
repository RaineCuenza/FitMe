package com.fitme.app.data.repository

import androidx.room.withTransaction
import com.fitme.app.data.local.*

class OutfitRepository(private val database: FitMeDatabase) {
    private val dao = database.outfitDao()
    val outfits = dao.observeAll()
    val plans = dao.observePlans()

    suspend fun find(id: Long) = dao.getById(id)

    suspend fun save(id: Long?, name: String, occasion: String, itemIds: List<Long>): Long {
        require(name.isNotBlank()) { "Enter an outfit name." }
        require(itemIds.isNotEmpty()) { "Select clothing items." }
        return database.withTransaction {
            val original = id?.let { dao.getById(it)?.outfit }
            if (id != null && original == null) error("Outfit no longer exists.")
            val outfitId = if (original != null) {
                dao.updateOutfit(original.copy(name = name.trim(), occasion = occasion))
                original.id
            } else {
                dao.insertOutfit(OutfitEntity(name = name.trim(), occasion = occasion))
            }
            dao.clearItems(outfitId)
            dao.insertItems(itemIds.distinct().map { OutfitItemCrossRef(outfitId, it) })
            outfitId
        }
    }

    suspend fun setFavorite(id: Long, favorite: Boolean) = dao.setFavorite(id, favorite)
    suspend fun delete(id: Long) = dao.deleteOutfit(id)
    suspend fun plan(outfitId: Long, date: String) = database.withTransaction {
        dao.deletePlansForDate(date)
        dao.addPlan(OutfitPlanEntity(outfitId = outfitId, plannedDate = date))
    }
    suspend fun removePlan(id: Long) = dao.deletePlan(id)
}
