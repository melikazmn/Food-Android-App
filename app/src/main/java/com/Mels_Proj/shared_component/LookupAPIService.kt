package com.Mels_Proj.shared_component

import com.Mels_Proj.feature.meal.domain.data.model.MealResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface LookupAPIService {
    @GET("lookup.php")
    suspend fun getMealById(@Query("i") id: String): Response<MealResponse>
}