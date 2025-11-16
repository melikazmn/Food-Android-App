package com.Mels_Proj
import com.Mels_Proj.feature.ai_recipe.presentation.AiRecipeFragment
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.Mels_Proj.databinding.ActivityMainBinding
import com.Mels_Proj.feature.favorites_screen.presentation.ui.FavoritesFragment
import com.Mels_Proj.feature.home_screen.presentation.ui.HomeFragment

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (savedInstanceState == null) {
            loadFragment(HomeFragment())
            binding.bottomNavigation.selectedItemId = R.id.navigation_home
        }

        binding.bottomNavigation.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.navigation_home -> {
                    loadFragment(HomeFragment()); true
                }
                R.id.navigation_favorites -> {
                    loadFragment(FavoritesFragment()); true
                }
                R.id.navigation_ai -> {
                    loadFragment(AiRecipeFragment()); true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .commit()
    }
}