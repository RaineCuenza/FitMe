package com.fitme.app

import android.app.Application
import androidx.room.Room
import com.fitme.app.data.local.FitMeDatabase
import com.fitme.app.data.repository.CatalogRepository
import com.fitme.app.data.repository.OutfitRepository
import com.fitme.app.data.remote.WeatherRepository

class FitMeApplication : Application() {
    val database: FitMeDatabase by lazy {
        Room.databaseBuilder(
            applicationContext,
            FitMeDatabase::class.java,
            "fitme_catalog.db"
        ).addMigrations(FitMeDatabase.MIGRATION_1_2).build()
    }

    val outfitRepository: OutfitRepository by lazy { OutfitRepository(database) }
    val weatherRepository: WeatherRepository by lazy { WeatherRepository() }
    val catalogRepository: CatalogRepository by lazy { CatalogRepository(database) }
}
