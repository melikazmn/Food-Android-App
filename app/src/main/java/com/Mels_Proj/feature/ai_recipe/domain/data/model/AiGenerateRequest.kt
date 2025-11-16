package com.Mels_Proj.feature.ai_recipe.data.model

data class AiGenerateRequest(
    val ingredients: List<String>,
    val servings: Int? = 2,
    val preference: String? = null
)
