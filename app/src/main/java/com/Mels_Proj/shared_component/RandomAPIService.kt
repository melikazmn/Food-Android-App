package com.Mels_Proj.shared_component

import com.Mels_Proj.feature.meal.domain.data.model.MealResponse
import retrofit2.Response
import retrofit2.http.GET

interface RandomAPIService {
    @GET("random.php")
    suspend fun getRandomMeal(): Response<MealResponse>
}