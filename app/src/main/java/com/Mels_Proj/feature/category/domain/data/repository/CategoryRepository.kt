package com.Mels_Proj.feature.category.domain.data.repository

import com.Mels_Proj.feature.category.domain.data.model.CategoriesResponse
import com.Mels_Proj.shared_component.CategoriesAPIService

class CategoryRepository(private val categoriesApi: CategoriesAPIService) {

    suspend fun getCategories(): Result<CategoriesResponse> {
        return try {
            val response = categoriesApi.getCategories()
            if (response.isSuccessful) {
                response.body()?.let { Result.success(it) } ?: Result.failure(Throwable("Response body is null"))
            } else {
                Result.failure(Throwable("API Error: ${response.code()}"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}