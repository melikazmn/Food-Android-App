package com.Mels_Proj.feature.home_screen.presentation.ui

import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.AttrRes
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions
import com.Mels_Proj.R
import com.Mels_Proj.databinding.FragmentHomeBinding
import com.Mels_Proj.feature.home_screen.presentation.ui.adapter.CategoryAdapter
import com.Mels_Proj.feature.home_screen.presentation.ui.adapter.AlphabetAdapter
import com.Mels_Proj.feature.home_screen.presentation.viewmodel.HomeViewModel
import com.Mels_Proj.feature.home_screen.presentation.viewmodel.HomeViewModelFactory
import com.Mels_Proj.feature.detail_screen.presentation.ui.DetailScreen
import com.Mels_Proj.feature.home_screen.presentation.ui.adapter.MealAdapter
import kotlin.random.Random

fun View.setVisible(isVisible: Boolean) {
    visibility = if (isVisible) View.VISIBLE else View.INVISIBLE
}

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var homeViewModel: HomeViewModel
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var mealAdapter: MealAdapter
    private lateinit var alphabetAdapter: AlphabetAdapter

    private val maxHorizontalTranslation = 200f
    private val maxVerticalTranslation = 110f

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(layoutInflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupViewModel()
        setupAdapters()
        setupListeners()
        setupObservers()
        callAPI()
    }



    private fun getThemeColor(@AttrRes attrRes: Int): Int {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(attrRes, typedValue, true)
        return typedValue.data
    }

    private fun setupViewModel() {
        val factory = HomeViewModelFactory(requireActivity().application)
        homeViewModel = ViewModelProvider(this, factory)[HomeViewModel::class.java]
    }



    private fun setupAdapters() {
        categoryAdapter = CategoryAdapter { category ->
            category.strCategory?.let {
                homeViewModel.filterByCategory(it)
                binding.rvMeals.smoothScrollToPosition(0)
                binding.searchView.setQuery("", false)
            }
        }
        binding.rvCategories.adapter = categoryAdapter
        binding.rvCategories.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)

        mealAdapter = MealAdapter(
            onItemClick = { meal ->
                Intent(requireContext(), DetailScreen::class.java).also {
                    it.putExtra("mealID", meal.idMeal)
                    startActivity(it)
                }
            },
            onFavoriteClick = { meal ->
                homeViewModel.toggleFavorite(meal)
            }
        )
        binding.rvMeals.adapter = mealAdapter
        binding.rvMeals.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        val snapHelper = LinearSnapHelper()
        snapHelper.attachToRecyclerView(binding.rvMeals)

        alphabetAdapter = AlphabetAdapter { letter ->
            binding.btnSearchLetter.animate()
                .scaleX(1.2f)
                .scaleY(1.2f)
                .setDuration(150)
                .withEndAction {
                    binding.btnSearchLetter.animate()
                        .scaleX(1f)
                        .scaleY(1f)
                        .setDuration(150)
                        .start()
                }.start()

            val surfaceColor = getThemeColor(com.google.android.material.R.attr.colorSurface)

            if (letter == "All") {
                homeViewModel.clearLetterFilter()
                binding.btnSearchLetter.text = "A"
                binding.btnSearchLetter.backgroundTintList = ColorStateList.valueOf(surfaceColor)
            } else {
                binding.searchView.setQuery("", false)
                homeViewModel.listMealsByFirstLetter(letter)
                binding.btnSearchLetter.text = letter
                binding.btnSearchLetter.backgroundTintList = ColorStateList.valueOf(ContextCompat.getColor(requireContext(), R.color.vibrant_purple))
            }
            binding.alphabetScrollerContainer.visibility = View.GONE
        }
        binding.rvAlphabetScroller.adapter = alphabetAdapter
    }

    private fun setupListeners() {

        binding.btnSearchLetter.setOnClickListener {
            val isVisible = binding.alphabetScrollerContainer.visibility == View.VISIBLE
            if (!isVisible) {
                binding.rvAlphabetScroller.scheduleLayoutAnimation()
            }
            binding.alphabetScrollerContainer.visibility = if (isVisible) View.GONE else View.VISIBLE
        }

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                binding.searchView.clearFocus()
                homeViewModel.searchMeal(query.toString())
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (newText.isNullOrEmpty()) {
                    homeViewModel.searchMeal(newText.toString())
                }
                return false
            }
        })
    }

    private fun setupObservers() {
        homeViewModel.bannerImageUrl.observe(viewLifecycleOwner) { imageUrl ->
            Glide.with(this)
                .load(imageUrl)
                .transition(DrawableTransitionOptions.withCrossFade())
                .into(binding.ivBanner)
            binding.ivBanner.animate().cancel()
            binding.ivBanner.translationX = 0f
            binding.ivBanner.translationY = 0f
            binding.ivBanner.animate()?.apply {
                duration = 10000
                translationX(
                    Random.nextDouble(
                        -maxHorizontalTranslation.toDouble(),
                        maxHorizontalTranslation.toDouble()
                    ).toFloat()
                )
                translationY(
                    Random.nextDouble(
                        -maxVerticalTranslation.toDouble(),
                        maxVerticalTranslation.toDouble()
                    ).toFloat()
                )
            }?.start()
        }
        homeViewModel.categories.observe(viewLifecycleOwner) { cats ->
            categoryAdapter.updateData(cats)
            binding.rvCategories.smoothScrollToPosition(0)
        }
        homeViewModel.meals.observe(viewLifecycleOwner) { meals ->
            mealAdapter.updateData(meals)
            binding.rvMeals.smoothScrollToPosition(0)
        }
        homeViewModel.allFavorites.observe(viewLifecycleOwner) { favorites ->
            val favoriteIds = favorites.mapNotNull { it.idMeal }.toSet()
            mealAdapter.updateFavorites(favoriteIds)
        }
        homeViewModel.catLoadingState.observe(viewLifecycleOwner) { isLoading ->
            binding.progCars.setVisible(isLoading)
            binding.rvCategories.setVisible(!isLoading)
        }
        homeViewModel.foodLoadingState.observe(viewLifecycleOwner) { isLoading ->
            binding.progFoods.setVisible(isLoading)
            binding.rvMeals.setVisible(!isLoading)
        }
        homeViewModel.toastMessage.observe(viewLifecycleOwner) {
            Toast.makeText(requireContext(), it, Toast.LENGTH_LONG).show()
        }
    }

    private fun callAPI(forceRefresh: Boolean = false) {
        binding.rvMeals.visibility = View.INVISIBLE
        binding.rvCategories.visibility = View.INVISIBLE
        homeViewModel.getCategories(forceRefresh)
        homeViewModel.getRandomMeals(forceRefresh)
    }

}