package com.Mels_Proj.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.Mels_Proj.db.dao.MealDao
import com.Mels_Proj.feature.meal.domain.data.model.FavoriteMealEntity

@Database(entities = [FavoriteMealEntity::class], version = 1)
abstract class MealDatabase: RoomDatabase() {

    abstract fun mealDao(): MealDao

    companion object {
        @Volatile
        private var INSTANCE: MealDatabase? = null

        fun getInstance(context: Context): MealDatabase {

            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        MealDatabase::class.java,
                        "meal_database"
                    ).build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}