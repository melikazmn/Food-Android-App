package com.Mels_Proj.feature.meal.domain.data.repository

import com.Mels_Proj.db.dao.MealDao
import com.Mels_Proj.feature.meal.domain.data.model.FavoriteMealEntity
import com.Mels_Proj.shared_component.SearchAPIService
import com.Mels_Proj.shared_component.FilterAPIService
import com.Mels_Proj.shared_component.LookupAPIService
import com.Mels_Proj.shared_component.RandomAPIService
import com.Mels_Proj.feature.meal.domain.data.model.MealResponse
import kotlinx.coroutines.flow.Flow

class MealRepository (
    private val searchApi: SearchAPIService,
    private val filterApi: FilterAPIService,
    private val lookupApi: LookupAPIService,
    private val randomApi: RandomAPIService,
    private val mealDao: MealDao? = null
) {

    suspend fun listMealsByFirstLetter(letter: String): Result<MealResponse> {
        return try {
            val response = searchApi.listMealsByFirstLetter(letter)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Throwable("Response body is null"))
            } else {
                Result.failure(Throwable("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchMeal(name: String): Result<MealResponse> {
        return try {
            val response = searchApi.searchMeal(name)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Throwable("Response body is null"))
            } else {
                Result.failure(Throwable("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun filterByCategory(category: String): Result<MealResponse> {
        return try {
            val response = filterApi.filterByCategory(category)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Throwable("Response body is null"))
            } else {
                Result.failure(Throwable("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getMealById(id: String): Result<MealResponse> {
        return try {
            val response = lookupApi.getMealById(id)
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Throwable("Response body is null"))
            } else {
                Result.failure(Throwable("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getRandomMeal(): Result<MealResponse>{
        return try {
            val response = randomApi.getRandomMeal()
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Throwable("Response body is null"))
            } else {
                Result.failure(Throwable("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun addFavorite(meal: FavoriteMealEntity) {
        mealDao?.insertFavorite(meal)
    }

    suspend fun removeFavorite(meal: FavoriteMealEntity) {
        mealDao?.deleteFavorite(meal)
    }

    fun getFavoriteMeal(mealId: String): Flow<FavoriteMealEntity?>? {
        return mealDao?.getFavoriteById(mealId)
    }

    fun getAllFavorites(): Flow<List<FavoriteMealEntity>>? {
        return mealDao?.getAllFavorites()
    }
}