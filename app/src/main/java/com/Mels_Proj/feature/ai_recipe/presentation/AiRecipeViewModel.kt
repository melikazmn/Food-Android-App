package com.Mels_Proj.feature.ai_recipe.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.Mels_Proj.feature.ai_recipe.data.AiRecipeRepository
import com.Mels_Proj.feature.meal.domain.data.model.MealResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

sealed class AiUiState {
    object Idle : AiUiState()
    object Loading : AiUiState()
    data class Success(val meal: MealResponse.MealResponseItem) : AiUiState()
    data class Error(val message: String) : AiUiState()
}

class AiRecipeViewModel(
    private val repo: AiRecipeRepository = AiRecipeRepository()
) : ViewModel() {
    private val _state = MutableStateFlow<AiUiState>(AiUiState.Idle)
    val state: StateFlow<AiUiState> = _state

    fun generateFromInput(input: String, servings: Int? = 2) {
        val ingredients = input.split(',', ';', '\n')
            .map { it.trim() }
            .filter { it.isNotEmpty() }
        if (ingredients.isEmpty()) {
            _state.value = AiUiState.Error("Please enter at least one ingredient.")
            return
        }
        _state.value = AiUiState.Loading
        viewModelScope.launch {
            try {
                val meal = repo.generate(ingredients, servings)
                _state.value = AiUiState.Success(meal)
            } catch (e: Exception) {
                _state.value = AiUiState.Error(e.message ?: "Something went wrong")
            }
        }
    }
}
