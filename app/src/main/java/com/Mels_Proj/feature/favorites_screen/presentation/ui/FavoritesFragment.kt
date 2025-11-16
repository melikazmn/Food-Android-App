package com.Mels_Proj.feature.favorites_screen.presentation.ui

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.Mels_Proj.databinding.FragmentFavoritesBinding
import com.Mels_Proj.feature.favorites_screen.presentation.ui.adapter.FavoritesAdapter
import com.Mels_Proj.feature.favorites_screen.presentation.viewmodel.FavoritesViewModel
import com.Mels_Proj.feature.favorites_screen.presentation.viewmodel.FavoritesViewModelFactory
import com.Mels_Proj.feature.detail_screen.presentation.ui.DetailScreen

class FavoritesFragment : Fragment() {

    private lateinit var binding: FragmentFavoritesBinding
    private lateinit var viewModel: FavoritesViewModel
    private lateinit var favoritesAdapter: FavoritesAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupRecyclerView()
        setupSearch()
        setupObservers()

        viewModel.loadFavorites()
    }

    private fun setupViewModel() {
        val factory = FavoritesViewModelFactory(requireActivity().application)
        viewModel = ViewModelProvider(this, factory)[FavoritesViewModel::class.java]
    }

    private fun setupRecyclerView() {
        favoritesAdapter = FavoritesAdapter { meal ->
            Intent(requireContext(), DetailScreen::class.java).also {
                it.putExtra("mealID", meal.idMeal)
                startActivity(it)
            }
        }
        binding.rvFavorites.apply {
            adapter = favoritesAdapter
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }
    }

    private fun setupSearch() {
        // XML id: search_view_favorites -> binding: searchViewFavorites
        binding.searchViewFavorites.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                viewModel.searchFavorites(newText.orEmpty())
                return true
            }
        })
    }

    private fun setupObservers() {
        viewModel.favoritesList.observe(viewLifecycleOwner) { favorites ->
            if (favorites.isNullOrEmpty()) {
                binding.rvFavorites.visibility = View.GONE
                binding.emptyStateView.visibility = View.VISIBLE
            } else {
                binding.emptyStateView.visibility = View.GONE
                binding.rvFavorites.visibility = View.VISIBLE
                favoritesAdapter.updateData(favorites)
            }
        }
    }
}
