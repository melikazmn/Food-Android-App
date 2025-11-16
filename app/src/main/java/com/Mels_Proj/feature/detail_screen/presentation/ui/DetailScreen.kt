package com.Mels_Proj.feature.detail_screen.presentation.ui

import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.Mels_Proj.R
import com.Mels_Proj.databinding.ScreenDetailBinding
import com.Mels_Proj.feature.detail_screen.ui.HowToViewModel
import com.Mels_Proj.feature.detail_screen.ui.HowToViewModelFactory
import com.Mels_Proj.feature.meal.domain.data.model.MealResponse

class DetailScreen : AppCompatActivity() {

    //region props
    private lateinit var binding: ScreenDetailBinding
    private lateinit var viewModel: HowToViewModel
    //endregion

    //region lc
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        initialBinding()
        initialViewModel() // This function is now corrected
        fetchData()
        configObservers()

    }
    //endregion

    //region methods
    private fun initialBinding() {
        binding = ScreenDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    private fun initialViewModel() {
        // ✨ FIX: Use the factory to create the ViewModel
        val factory = HowToViewModelFactory(application)
        viewModel = ViewModelProvider(this, factory)[HowToViewModel::class.java]
    }

    private fun fetchData() {
        val mealId = intent.getStringExtra("mealID")
        if (mealId == null) {
            Toast.makeText(this, "Error: Meal ID not found", Toast.LENGTH_LONG).show()
            finish()
            return
        }
        viewModel.loadMealDetails(mealId)
    }

    private fun configObservers() {
        viewModel.loadingState.observe(this) { isLoading ->
            binding.prog2.visibility = if (isLoading) View.VISIBLE else View.GONE
            val contentVisibility = if (isLoading) View.INVISIBLE else View.VISIBLE
            binding.tvFoodArea.visibility = contentVisibility
            binding.tvFoodCategory.visibility = contentVisibility
            binding.tvFoodTitle.visibility = contentVisibility
            binding.tvInstructions.visibility = contentVisibility
        }

        viewModel.meal.observe(this) { meal ->
            meal?.let { bindMealData(it) }
        }

        viewModel.toastMessage.observe(this) { message ->
            Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
        }

        viewModel.isFavorite.observe(this) { isFavorite ->
            if (isFavorite) {
                binding.btnFavorite.setColorFilter(Color.RED)
            } else {
                binding.btnFavorite.setColorFilter(Color.BLACK)
            }
        }
    }

    private fun bindMealData(mealResponseItem: MealResponse.MealResponseItem) {
        binding.apply {
            tvFoodTitle.text = mealResponseItem.strMeal
            tvFoodCategory.text = mealResponseItem.strCategory
            tvFoodArea.text = mealResponseItem.strArea
            tvInstructions.text = mealResponseItem.strInstructions?.replace("\r\n", "\n")

            Glide.with(this@DetailScreen)
                .load(mealResponseItem.strMealThumb)
                .into(ivFoodImageDetail)

            setupClickListeners(mealResponseItem)
            populateIngredients(mealResponseItem)
        }
    }

    private fun setupClickListeners(mealResponseItem: MealResponse.MealResponseItem) {
        binding.btnBack.setOnClickListener {
            finish()
        }

        binding.ivYoutube.setOnClickListener {
            if (!mealResponseItem.strYoutube.isNullOrBlank()) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(mealResponseItem.strYoutube)))
            } else {
                Toast.makeText(this, "No YouTube video available", Toast.LENGTH_SHORT).show()
            }
        }



        binding.ivChrome.setOnClickListener {

            if (!mealResponseItem.strSource.isNullOrBlank()) {
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(mealResponseItem.strSource)))
            } else {
                Toast.makeText(this, "No source link available", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnFavorite.setOnClickListener {
            it.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(150)
                .withEndAction {
                    // Animate back to the original size
                    it.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(150)
                        .start()
                }.start()
            viewModel.onFavoriteClicked()
        }
    }

    private fun populateIngredients(mealResponseItem: MealResponse.MealResponseItem) {
        binding.llIngredientsContainer.removeAllViews()
        val ingredients = createIngredientsList(mealResponseItem)
        val inflater = LayoutInflater.from(this)

        for ((ingredient, measure) in ingredients) {
            val ingredientView = inflater.inflate(R.layout.item_ingredient, binding.llIngredientsContainer, false)
            val ingredientName = ingredientView.findViewById<TextView>(R.id.tv_ingredient_name)
            val ingredientMeasure = ingredientView.findViewById<TextView>(R.id.tv_ingredient_measure)
            ingredientName.text = ingredient
            ingredientMeasure.text = measure
            binding.llIngredientsContainer.addView(ingredientView)
        }
    }

    private fun createIngredientsList(mealResponseItem: MealResponse.MealResponseItem): List<Pair<String, String>> {
        return buildList {
            if (!mealResponseItem.strIngredient1.isNullOrBlank()) add(mealResponseItem.strIngredient1 to (mealResponseItem.strMeasure1 ?: ""))
            if (!mealResponseItem.strIngredient2.isNullOrBlank()) add(mealResponseItem.strIngredient2 to (mealResponseItem.strMeasure2 ?: ""))
            if (!mealResponseItem.strIngredient3.isNullOrBlank()) add(mealResponseItem.strIngredient3 to (mealResponseItem.strMeasure3 ?: ""))
            if (!mealResponseItem.strIngredient4.isNullOrBlank()) add(mealResponseItem.strIngredient4 to (mealResponseItem.strMeasure4 ?: ""))
            if (!mealResponseItem.strIngredient5.isNullOrBlank()) add(mealResponseItem.strIngredient5 to (mealResponseItem.strMeasure5 ?: ""))
            if (!mealResponseItem.strIngredient6.isNullOrBlank()) add(mealResponseItem.strIngredient6 to (mealResponseItem.strMeasure6 ?: ""))
            if (!mealResponseItem.strIngredient7.isNullOrBlank()) add(mealResponseItem.strIngredient7 to (mealResponseItem.strMeasure7 ?: ""))
            if (!mealResponseItem.strIngredient8.isNullOrBlank()) add(mealResponseItem.strIngredient8 to (mealResponseItem.strMeasure8 ?: ""))
            if (!mealResponseItem.strIngredient9.isNullOrBlank()) add(mealResponseItem.strIngredient9 to (mealResponseItem.strMeasure9 ?: ""))
            if (!mealResponseItem.strIngredient10.isNullOrBlank()) add(mealResponseItem.strIngredient10 to (mealResponseItem.strMeasure10 ?: ""))
            if (!mealResponseItem.strIngredient11.isNullOrBlank()) add(mealResponseItem.strIngredient11 to (mealResponseItem.strMeasure11 ?: ""))
            if (!mealResponseItem.strIngredient12.isNullOrBlank()) add(mealResponseItem.strIngredient12 to (mealResponseItem.strMeasure12 ?: ""))
            if (!mealResponseItem.strIngredient13.isNullOrBlank()) add(mealResponseItem.strIngredient13 to (mealResponseItem.strMeasure13 ?: ""))
            if (!mealResponseItem.strIngredient14.isNullOrBlank()) add(mealResponseItem.strIngredient14 to (mealResponseItem.strMeasure14 ?: ""))
            if (!mealResponseItem.strIngredient15.isNullOrBlank()) add(mealResponseItem.strIngredient15 to (mealResponseItem.strMeasure15 ?: ""))
            if (!mealResponseItem.strIngredient16.isNullOrBlank()) add(mealResponseItem.strIngredient16 to (mealResponseItem.strMeasure16 ?: ""))
            if (!mealResponseItem.strIngredient17.isNullOrBlank()) add(mealResponseItem.strIngredient17 to (mealResponseItem.strMeasure17 ?: ""))
            if (!mealResponseItem.strIngredient18.isNullOrBlank()) add(mealResponseItem.strIngredient18 to (mealResponseItem.strMeasure18 ?: ""))
            if (!mealResponseItem.strIngredient19.isNullOrBlank()) add(mealResponseItem.strIngredient19 to (mealResponseItem.strMeasure19 ?: ""))
            if (!mealResponseItem.strIngredient20.isNullOrBlank()) add(mealResponseItem.strIngredient20 to (mealResponseItem.strMeasure20 ?: ""))
        }
    }
    //endregion
}