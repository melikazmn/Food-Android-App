package com.Mels_Proj.feature.ai_recipe.presentation

import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.RequestOptions
import com.Mels_Proj.R
import com.Mels_Proj.databinding.FragmentAiRecipeBinding
import com.Mels_Proj.feature.meal.domain.data.model.FavoriteMealEntity
import com.Mels_Proj.feature.meal.domain.data.model.MealResponse
import com.Mels_Proj.feature.meal.domain.data.repository.MealRepository
import com.Mels_Proj.feature.meal.domain.data.toFavoriteEntity
import com.Mels_Proj.db.MealDatabase
import com.Mels_Proj.shared_component.API
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class AiRecipeFragment : Fragment() {

    private var _binding: FragmentAiRecipeBinding? = null
    private val binding get() = _binding!!
    private val vm: AiRecipeViewModel by viewModels()

    // --- Local repo + favorites (so we can run toggleFavorite here) ---
    private lateinit var mealRepository: MealRepository
    private val _allFavorites = MutableLiveData<List<FavoriteMealEntity>>(emptyList())

    // Keep last generated meal for the favorite action
    private var lastMeal: MealResponse.MealResponseItem? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAiRecipeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        // Build repository exactly like HomeViewModel does
        val mealDao = MealDatabase.getInstance(requireContext().applicationContext).mealDao()
        mealRepository = MealRepository(
            searchApi = API.searchApiService,
            filterApi = API.filterApiService,
            lookupApi = API.lookupApiService,
            randomApi = API.randomApiService,
            mealDao = mealDao
        )

        // Observe favorites so toggleFavorite can check current state
        viewLifecycleOwner.lifecycleScope.launch {
            mealRepository.getAllFavorites()?.collectLatest { favorites ->
                _allFavorites.postValue(favorites ?: emptyList())
            }
        }

        // Scroll bars in long instructions
        binding.tvInstructions.isVerticalScrollBarEnabled = true

        // Generate click
        binding.btnGenerate.setOnClickListener {
            val ingredients = binding.etIngredients.text?.toString().orEmpty().trim()
            if (ingredients.isEmpty()) {
                Toast.makeText(requireContext(), "Please enter some ingredients.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Hide keyboard & clear focus
            val imm = requireContext().getSystemService(InputMethodManager::class.java)
            imm.hideSoftInputFromWindow(binding.etIngredients.windowToken, 0)
            binding.etIngredients.clearFocus()

            vm.generateFromInput(ingredients, servings = 2)
        }

        // Favorite click (Fragment-local toggleFavorite)
        binding.btnFavorite.setOnClickListener {
            val meal = lastMeal
            if (meal == null) {
                Toast.makeText(requireContext(), "No recipe yet.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            toggleFavorite(meal)
        }

        // Observe AI state
        viewLifecycleOwner.lifecycleScope.launch {
            vm.state.collectLatest { state ->
                when (state) {
                    is AiUiState.Idle -> {
                        showLoading(false)
                        binding.btnFavorite.visibility = View.GONE
                    }
                    is AiUiState.Loading -> {
                        showLoading(true)
                        binding.btnFavorite.visibility = View.GONE
                    }
                    is AiUiState.Error -> {
                        showLoading(false)
                        binding.btnFavorite.visibility = View.GONE
                        Toast.makeText(requireContext(), state.message, Toast.LENGTH_SHORT).show()
                    }
                    is AiUiState.Success -> {
                        showLoading(false)
                        lastMeal = state.meal
                        renderMeal(state.meal)
                        binding.btnFavorite.visibility = View.VISIBLE
                    }
                }
            }
        }
    }

    private fun showLoading(show: Boolean) {
        binding.progress.visibility = if (show) View.VISIBLE else View.GONE
        binding.btnGenerate.isEnabled = !show
    }

    private fun renderMeal(item: MealResponse.MealResponseItem) {
        binding.tvTitle.text = item.strMeal ?: getString(R.string.app_name)

        val thumb = item.strMealThumb
        if (thumb.isNullOrBlank()) {
            binding.imgMeal.setImageResource(R.drawable.ic_round_fastfood_24)
        } else if (thumb.startsWith("data:image")) {
            try {
                val base64 = thumb.substringAfter("base64,", "")
                val bytes = Base64.decode(base64, Base64.DEFAULT)
                val bmp = BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                Glide.with(binding.imgMeal)
                    .asBitmap()
                    .load(bmp)
                    .apply(RequestOptions.bitmapTransform(RoundedCorners(24)))
                    .placeholder(R.drawable.ic_round_fastfood_24)
                    .error(R.drawable.ic_round_fastfood_24)
                    .into(binding.imgMeal)
            } catch (_: Throwable) {
                binding.imgMeal.setImageResource(R.drawable.ic_round_fastfood_24)
            }
        } else {
            Glide.with(binding.imgMeal)
                .load(thumb)
                .apply(RequestOptions.bitmapTransform(RoundedCorners(24)))
                .placeholder(R.drawable.ic_round_fastfood_24)
                .error(R.drawable.ic_round_fastfood_24)
                .into(binding.imgMeal)
        }

        // Ingredients 1..20
        val lines = buildList {
            fun addIf(name: String?, measure: String?) {
                if (!name.isNullOrBlank()) add(if (!measure.isNullOrBlank()) "$name — $measure" else name)
            }
            addIf(item.strIngredient1, item.strMeasure1)
            addIf(item.strIngredient2, item.strMeasure2)
            addIf(item.strIngredient3, item.strMeasure3)
            addIf(item.strIngredient4, item.strMeasure4)
            addIf(item.strIngredient5, item.strMeasure5)
            addIf(item.strIngredient6, item.strMeasure6)
            addIf(item.strIngredient7, item.strMeasure7)
            addIf(item.strIngredient8, item.strMeasure8)
            addIf(item.strIngredient9, item.strMeasure9)
            addIf(item.strIngredient10, item.strMeasure10)
            addIf(item.strIngredient11, item.strMeasure11)
            addIf(item.strIngredient12, item.strMeasure12)
            addIf(item.strIngredient13, item.strMeasure13)
            addIf(item.strIngredient14, item.strMeasure14)
            addIf(item.strIngredient15, item.strMeasure15)
            addIf(item.strIngredient16, item.strMeasure16)
            addIf(item.strIngredient17, item.strMeasure17)
            addIf(item.strIngredient18, item.strMeasure18)
            addIf(item.strIngredient19, item.strMeasure19)
            addIf(item.strIngredient20, item.strMeasure20)
        }
        binding.tvIngredients.text = lines.joinToString("\n") { "• $it" }
        binding.tvInstructions.text = item.strInstructions.orEmpty()
    }

    /**
     * Your exact toggleFavorite body, adapted to call the Fragment's
     * local mealRepository and _allFavorites.
     */
    private fun toggleFavorite(meal: MealResponse.MealResponseItem) {
        viewLifecycleOwner.lifecycleScope.launch {
            val isCurrentlyFavorite =
                _allFavorites.value?.any { it.idMeal == meal.idMeal } == true

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

    private fun onSomeError(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
