package com.Mels_Proj.feature.ai_recipe.data

import com.Mels_Proj.feature.ai_recipe.data.model.AiGenerateRequest
import com.Mels_Proj.feature.ai_recipe.data.remote.AiNetwork
import com.Mels_Proj.feature.meal.domain.data.model.MealResponse

class AiRecipeRepository {
    private val api = AiNetwork.api

    suspend fun generate(ingredients: List<String>, servings: Int? = 2): MealResponse.MealResponseItem {
        val clean = ingredients.map { it.trim() }.filter { it.isNotEmpty() }
        return api.generateRecipe(AiGenerateRequest(clean, servings))
    }
}
