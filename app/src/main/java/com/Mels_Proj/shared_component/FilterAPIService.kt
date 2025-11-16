package com.Mels_Proj.shared_component

import com.Mels_Proj.feature.meal.domain.data.model.MealResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface FilterAPIService {
    @GET("filter.php")
    suspend fun filterByCategory(@Query("c") category: String): Response<MealResponse>
}