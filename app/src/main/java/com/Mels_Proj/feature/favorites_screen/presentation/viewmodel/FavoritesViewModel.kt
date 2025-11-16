package com.Mels_Proj.feature.favorites_screen.presentation.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.Mels_Proj.db.MealDatabase
import com.Mels_Proj.feature.meal.domain.data.model.FavoriteMealEntity
import com.Mels_Proj.feature.meal.domain.data.repository.MealRepository
import com.Mels_Proj.shared_component.API
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoritesViewModel(
    application: Application,
    private val mealRepository: MealRepository
) : AndroidViewModel(application) {

    private var allFavorites: List<FavoriteMealEntity> = emptyList()

    private val _favoritesList = MutableLiveData<List<FavoriteMealEntity>>()
    val favoritesList: LiveData<List<FavoriteMealEntity>> get() = _favoritesList

    fun loadFavorites() {
        viewModelScope.launch {
            mealRepository.getAllFavorites()?.collectLatest { favorites ->
                allFavorites = favorites
                _favoritesList.postValue(allFavorites)
            }
        }
    }

    fun searchFavorites(query: String) {
        if (query.isBlank()) {
            _favoritesList.postValue(allFavorites)
        } else {
            val filteredList = allFavorites.filter {
                it.strMeal?.contains(query, ignoreCase = true) == true
            }
            _favoritesList.postValue(filteredList)
        }
    }
}

//region helper_classes
class FavoritesModule(application: Application) {
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

class FavoritesViewModelFactory(private val application: Application) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(FavoritesViewModel::class.java)){
            val module = FavoritesModule(application)
            return FavoritesViewModel(application, module.mealRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
//endregion