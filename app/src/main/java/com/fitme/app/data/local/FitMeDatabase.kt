package com.fitme.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(
    entities = [ClothingItemEntity::class, OutfitEntity::class, OutfitItemCrossRef::class, OutfitPlanEntity::class],
    version = 2,
    exportSchema = false
)
abstract class FitMeDatabase : RoomDatabase() {
    abstract fun clothingDao(): ClothingDao
    abstract fun outfitDao(): OutfitDao

    companion object {
        /** Retains the existing clothing catalog when adding saved outfits and planning. */
        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL("CREATE TABLE IF NOT EXISTS `outfits` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `name` TEXT NOT NULL, `occasion` TEXT NOT NULL, `createdAt` INTEGER NOT NULL, `isFavorite` INTEGER NOT NULL)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `outfit_items` (`outfitId` INTEGER NOT NULL, `clothingItemId` INTEGER NOT NULL, PRIMARY KEY(`outfitId`, `clothingItemId`), FOREIGN KEY(`outfitId`) REFERENCES `outfits`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE, FOREIGN KEY(`clothingItemId`) REFERENCES `clothing_items`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_outfit_items_outfitId` ON `outfit_items` (`outfitId`)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_outfit_items_clothingItemId` ON `outfit_items` (`clothingItemId`)")
                db.execSQL("CREATE TABLE IF NOT EXISTS `outfit_plans` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `outfitId` INTEGER NOT NULL, `plannedDate` TEXT NOT NULL, `notes` TEXT NOT NULL, FOREIGN KEY(`outfitId`) REFERENCES `outfits`(`id`) ON UPDATE NO ACTION ON DELETE CASCADE)")
                db.execSQL("CREATE INDEX IF NOT EXISTS `index_outfit_plans_outfitId` ON `outfit_plans` (`outfitId`)")
            }
        }
    }
}
