package com.Mels_Proj.feature.detail_screen.ui

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.Mels_Proj.db.MealDatabase
import com.Mels_Proj.feature.meal.domain.data.model.MealResponse
import com.Mels_Proj.feature.meal.domain.data.repository.MealRepository
import com.Mels_Proj.feature.meal.domain.data.toFavoriteEntity
import com.Mels_Proj.feature.meal.domain.data.toMealResponseItem
import com.Mels_Proj.shared_component.API
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class HowToViewModel(
    application: Application,
    private val mealRepository: MealRepository
) : AndroidViewModel(application) {

    //region props
    val meal: LiveData<MealResponse.MealResponseItem?> get() = _meal
    private val _meal = MutableLiveData<MealResponse.MealResponseItem?>()

    val loadingState: LiveData<Boolean> get() = _loadingState
    private val _loadingState = MutableLiveData<Boolean>()

    val toastMessage: LiveData<String> get() = _toastMessage
    private val _toastMessage = MutableLiveData<String>()

    val isFavorite: LiveData<Boolean> get() = _isFavorite
    private val _isFavorite = MutableLiveData(false)
    //endregion

    //region methods
    fun loadMealDetails(mealId: String) {
        viewModelScope.launch {
            _loadingState.postValue(true)

            val favoriteMeal = mealRepository.getFavoriteMeal(mealId)?.first()

            if (favoriteMeal != null) {
                _meal.postValue(favoriteMeal.toMealResponseItem())
                _isFavorite.postValue(true)
                _loadingState.postValue(false)
            } else {
                _isFavorite.postValue(false)
                fetchMealFromApi(mealId)
            }
        }
    }

    private suspend fun fetchMealFromApi(id: String) {
        mealRepository.getMealById(id).onSuccess { response ->
            if (response.mealList.isNullOrEmpty()) {
                _toastMessage.postValue("No meal found with ID: $id")
            } else {
                _meal.postValue(response.mealList.first())
            }
        }.onFailure { exception ->
            _toastMessage.postValue("API Error: ${exception.message}")
        }
        _loadingState.postValue(false)
    }

    fun onFavoriteClicked() {
        viewModelScope.launch {
            val currentMeal = _meal.value ?: return@launch
            val favoriteEntity = currentMeal.toFavoriteEntity() ?: return@launch

            if (_isFavorite.value == true) {
                mealRepository.removeFavorite(favoriteEntity)
                _toastMessage.postValue("Removed from favorites")
                _isFavorite.postValue(false)
            } else {
                mealRepository.addFavorite(favoriteEntity)
                _toastMessage.postValue("Added to favorites")
                _isFavorite.postValue(true)
            }
        }
    }
    //endregion
}

//region helper_classes
class HowToModule(application: Application) {
    val mealRepository: MealRepository by lazy {
        val mealDao = MealDatabase.getInstance(application).mealDao()
        MealRepository(
            searchApi = API.searchApiService,
            filterApi = API.filterApiService,
            lookupApi = API.lookupApiService,
            randomApi = API.randomApiService,
            mealDao = mealDao
        )
    }
}

class HowToViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HowToViewModel::class.java)){
            val module = HowToModule(application)
            return HowToViewModel(application, module.mealRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
//endregion