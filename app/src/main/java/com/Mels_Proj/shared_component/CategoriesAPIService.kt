package com.Mels_Proj.shared_component

import com.Mels_Proj.feature.category.domain.data.model.CategoriesResponse
import retrofit2.Response
import retrofit2.http.GET

interface CategoriesAPIService {
    @GET("categories.php")
    suspend fun getCategories(): Response<CategoriesResponse>
}