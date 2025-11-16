package com.Mels_Proj.feature.favorites_screen.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.Mels_Proj.databinding.ItemFavoriteMealBinding
import com.Mels_Proj.feature.meal.domain.data.model.FavoriteMealEntity

class FavoritesAdapter(
    private val onItemClick: (FavoriteMealEntity) -> Unit
) : RecyclerView.Adapter<FavoritesAdapter.ViewHolder>() {

    private var items: List<FavoriteMealEntity> = emptyList()

    fun updateData(newItems: List<FavoriteMealEntity>) {
        items = newItems
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemFavoriteMealBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemFavoriteMealBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(items[adapterPosition])
                }
            }
        }

        fun bind(item: FavoriteMealEntity) = with(binding) {
            // IDs from your new XML:
            // tv_meal_name -> tvMealName
            // tv_meal_category -> tvMealCategory
            // iv_meal_image -> ivMealImage
            tvMealName.text = item.strMeal
            tvMealCategory.text = item.strCategory
            // No area or favorite button in the new layout; nothing to set/hide there.

            Glide.with(root.context)
                .load(item.strMealThumb)
                .centerCrop()
                .into(ivMealImage)
        }
    }
}
