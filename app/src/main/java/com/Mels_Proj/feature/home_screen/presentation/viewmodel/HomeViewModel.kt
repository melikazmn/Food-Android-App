package com.Mels_Proj.feature.home_screen.presentation.viewmodel

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.Mels_Proj.db.MealDatabase
import com.Mels_Proj.feature.category.domain.data.model.CategoriesResponse
import com.Mels_Proj.feature.category.domain.data.repository.CategoryRepository
import com.Mels_Proj.feature.meal.domain.data.model.FavoriteMealEntity
import com.Mels_Proj.feature.meal.domain.data.model.MealResponse
import com.Mels_Proj.feature.meal.domain.data.repository.MealRepository
import com.Mels_Proj.feature.meal.domain.data.toFavoriteEntity
import com.Mels_Proj.shared_component.API
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(
    application: Application,
    private val mealRepository: MealRepository,
    private val categoryRepository: CategoryRepository
) : AndroidViewModel(application) {

    //region LiveData
    val meals: LiveData<List<MealResponse.MealResponseItem>> get() = _meals
    private val _meals = MutableLiveData<List<MealResponse.MealResponseItem>>()

    val allFavorites: LiveData<List<FavoriteMealEntity>> get() = _allFavorites
    private val _allFavorites = MutableLiveData<List<FavoriteMealEntity>>()

    val categories: LiveData<List<CategoriesResponse.CategoryResponseItem>> get() = _categories
    private val _categories = MutableLiveData<List<CategoriesResponse.CategoryResponseItem>>()

    val bannerImageUrl: LiveData<String> get() = _bannerImageUrl
    private val _bannerImageUrl = MutableLiveData<String>()

    val foodLoadingState: LiveData<Boolean> get() = _foodLoadingState
    private val _foodLoadingState = MutableLiveData<Boolean>()

    val catLoadingState: LiveData<Boolean> get() = _catLoadingState
    private val _catLoadingState = MutableLiveData<Boolean>()

    val toastMessage: LiveData<String> get() = _toastMessage
    private val _toastMessage = MutableLiveData<String>()
    //endregion

    //region Props
    private var initialMealsList: List<MealResponse.MealResponseItem> = emptyList()
    private var currentFilter: String? = null
    private var currentLetter: String? = null
    private val homeBannerHandler = Handler(Looper.getMainLooper())
    private lateinit var homeBannerRunnable: Runnable
    //endregion

    init {
        observeFavorites()
    }

    private fun observeFavorites() {
        viewModelScope.launch {
            mealRepository.getAllFavorites()?.collectLatest { favorites ->
                _allFavorites.postValue(favorites)
            }
        }
    }

    //region Meal Methods
    fun getRandomMeals(forceRefresh: Boolean = false) {
        if (initialMealsList.isNotEmpty() && !forceRefresh) {
            _meals.postValue(initialMealsList)
//            startBannerCycling(initialMealsList)
            return
        }
        viewModelScope.launch {
            _foodLoadingState.postValue(true)
            val successfulMeals = (1..10)
                .map { async { mealRepository.getRandomMeal() } }
                .awaitAll()
                .mapNotNull { it.getOrNull()?.mealList?.firstOrNull() }
                .take(5)

            if (successfulMeals.isNotEmpty()) {
                startBannerCycling(successfulMeals)
                initialMealsList = successfulMeals
                _meals.postValue(initialMealsList)
            } else {
                onSomeError("Failed to load random meals")
            }
            _foodLoadingState.postValue(false)
        }
    }

    fun filterByCategory(category: String) {
        currentFilter = if (category == currentFilter) null else category
        applyFilters()
    }

    fun searchMeal(name: String) {
        applyFilters(searchTerm = name)
    }

    fun listMealsByFirstLetter(letter: String) {
        currentLetter = letter
        applyFilters()
    }

    fun clearLetterFilter() {
        currentLetter = null
        applyFilters()
    }

    fun toggleFavorite(meal: MealResponse.MealResponseItem) {
        viewModelScope.launch {
            val isCurrentlyFavorite = _allFavorites.value?.any { it.idMeal == meal.idMeal } == true

            if (isCurrentlyFavorite) {
                val favoriteEntity = meal.toFavoriteEntity() ?: return@launch
                mealRepository.removeFavorite(favoriteEntity)
            } else {
                if (meal.strInstructions.isNullOrBlank()) {
                    meal.idMeal?.let { mealId ->
                        mealRepository.getMealById(mealId).onSuccess { response ->
                            response.mealList.firstOrNull()?.let { fullMeal ->
                                fullMeal.toFavoriteEntity()?.let {
                                    mealRepository.addFavorite(it)
                                }
                            }
                        }.onFailure {
                            onSomeError("Failed to fetch full meal details.")
                        }
                    }
                } else {
                    meal.toFavoriteEntity()?.let {
                        mealRepository.addFavorite(it)
                    }
                }
            }
        }
    }

    //endregion

    //region Category Methods
    fun getCategories(forceRefresh: Boolean = false) {
        if (_categories.value?.isNotEmpty() == true && !forceRefresh) {
            return
        }
        viewModelScope.launch {
            _catLoadingState.postValue(true)
            categoryRepository.getCategories().onSuccess {
                _categories.postValue(it.categoryList)
            }.onFailure {
                onSomeError(it.message.toString())
            }
            _catLoadingState.postValue(false)
        }
    }
    //endregion

    //region Private & Lifecycle
    private fun applyFilters(searchTerm: String? = null) {
        viewModelScope.launch {
            _foodLoadingState.postValue(true)
            val result = runCatching {
                if (currentFilter == null && currentLetter == null && searchTerm.isNullOrBlank()) {
                    _meals.postValue(initialMealsList)
                    _foodLoadingState.postValue(false)
                    return@launch
                }

                val baseMeals = when {
                    currentFilter != null ->
                        mealRepository.filterByCategory(currentFilter!!).getOrNull()?.mealList
                    currentLetter != null ->
                        mealRepository.listMealsByFirstLetter(currentLetter!!).getOrNull()?.mealList
                    !searchTerm.isNullOrBlank() ->
                        mealRepository.searchMeal(searchTerm).getOrNull()?.mealList
                    else -> initialMealsList
                } ?: emptyList()

                var finalMeals = baseMeals

                if (!currentFilter.isNullOrBlank()) {
                    currentLetter?.let { letter ->
                        finalMeals =
                            finalMeals.filter { it.strMeal?.startsWith(letter, ignoreCase = true) == true }
                    }

                    searchTerm?.let { st ->
                        finalMeals = finalMeals.filter { it.strMeal?.contains(st, true) == true }
                    }
                }

                if (currentFilter.isNullOrBlank() && !currentLetter.isNullOrBlank()) {
                    searchTerm?.let { st ->
                        finalMeals = finalMeals.filter { it.strMeal?.contains(st, true) == true }
                    }
                }

                if (finalMeals.isEmpty()) {
                    onSomeError("No meals found with the current filters.")
                }
                _meals.postValue(finalMeals.take(5))
            }

            result.onFailure {
                onSomeError(it.message ?: "An unknown error occurred.")
                _meals.postValue(emptyList())
            }
            _foodLoadingState.postValue(false)
        }
    }

    private fun startBannerCycling(mealsForPics: List<MealResponse.MealResponseItem>) {
        if (mealsForPics.isEmpty()) return
        if (::homeBannerRunnable.isInitialized) homeBannerHandler.removeCallbacks(homeBannerRunnable)
        homeBannerRunnable = Runnable {
            mealsForPics.random().strMealThumb?.let { url ->
                _bannerImageUrl.postValue(url)
            }
            homeBannerHandler.postDelayed(homeBannerRunnable, 10000)
        }
        homeBannerHandler.post(homeBannerRunnable)
    }

    private fun onSomeError(message: String = "An unexpected error occurred.") {
        _toastMessage.postValue(message)
    }

    override fun onCleared() {
        super.onCleared()
        if (::homeBannerRunnable.isInitialized) {
            homeBannerHandler.removeCallbacks(homeBannerRunnable)
        }
    }
    //endregion
}

//region helper_classes

class HomeModule(application: Application){
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

    val categoryRepository: CategoryRepository by lazy {
        CategoryRepository(API.categoriesApiService)
    }
}

class HomeViewModelFactory(private val application: Application) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            val module = HomeModule(application)
            return HomeViewModel(application, module.mealRepository, module.categoryRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
//endregion