package com.Mels_Proj.shared_component

import com.Mels_Proj.feature.meal.domain.data.model.MealResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface SearchAPIService {
    @GET("search.php")
    suspend fun listMealsByFirstLetter(@Query("f") letter: String): Response<MealResponse>

    @GET("search.php")
    suspend fun searchMeal(@Query("s") name: String): Response<MealResponse>
}