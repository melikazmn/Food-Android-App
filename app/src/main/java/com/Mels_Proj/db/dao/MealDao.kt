package com.Mels_Proj.db.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.Mels_Proj.feature.meal.domain.data.Constants
import com.Mels_Proj.feature.meal.domain.data.model.FavoriteMealEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MealDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favoriteMeal: FavoriteMealEntity)

    @Delete
    suspend fun deleteFavorite(favoriteMeal: FavoriteMealEntity)

    @Query("SELECT * FROM ${Constants.FAVORITE_MEALS_TABLE_NAME} WHERE ${Constants.MEAL_ID_COLUMN} = :mealId")
    fun getFavoriteById(mealId: String): Flow<FavoriteMealEntity?>

    @Query("SELECT * FROM ${Constants.FAVORITE_MEALS_TABLE_NAME}")
    fun getAllFavorites(): Flow<List<FavoriteMealEntity>>
}