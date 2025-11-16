package com.Mels_Proj.feature.ai_recipe.data.remote

import com.Mels_Proj.feature.ai_recipe.data.model.AiGenerateRequest
import com.Mels_Proj.feature.meal.domain.data.model.MealResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AiRecipeApi {
    @POST("/ai/recipe")
    suspend fun generateRecipe(@Body body: AiGenerateRequest): MealResponse.MealResponseItem
}
