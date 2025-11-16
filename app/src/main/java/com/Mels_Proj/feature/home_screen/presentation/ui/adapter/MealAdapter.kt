package com.Mels_Proj.feature.home_screen.presentation.ui.adapter

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.Mels_Proj.databinding.ItemHomeMealBinding
import com.Mels_Proj.feature.meal.domain.data.model.MealResponse

class MealAdapter(
    private val onItemClick: (MealResponse.MealResponseItem) -> Unit,
    private val onFavoriteClick: (MealResponse.MealResponseItem) -> Unit
) : RecyclerView.Adapter<MealAdapter.ViewHolder>() {

    private var items: List<MealResponse.MealResponseItem> = emptyList()
    private var favoriteMealIds: Set<String> = emptySet()

    @SuppressLint("NotifyDataSetChanged")
    fun updateData(newItems: List<MealResponse.MealResponseItem>) {
        items = newItems
        notifyDataSetChanged()

    }

    fun updateFavorites(newFavoriteIds: Set<String>) {
        val oldFavoriteIds = favoriteMealIds
        favoriteMealIds = newFavoriteIds

        items.forEachIndexed { index, meal ->
            val mealId = meal.idMeal ?: return@forEachIndexed
            val wasFavorite = oldFavoriteIds.contains(mealId)
            val isNowFavorite = newFavoriteIds.contains(mealId)
            if (wasFavorite != isNowFavorite) {
                notifyItemChanged(index, PAYLOAD_FAVORITE_STATUS_CHANGED)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHomeMealBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        if (payloads.contains(PAYLOAD_FAVORITE_STATUS_CHANGED)) {
            holder.updateFavoriteStatus(favoriteMealIds.contains(items[position].idMeal))
        } else {
            super.onBindViewHolder(holder, position, payloads)
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemHomeMealBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    onItemClick(items[adapterPosition])
                }
            }

            binding.btnFavorite.setOnClickListener {
                if (adapterPosition != RecyclerView.NO_POSITION) {
                    val meal = items[adapterPosition]
                    it.animate()
                        .scaleX(1.2f)
                        .scaleY(1.2f)
                        .setDuration(150)
                        .withEndAction {
                            it.animate()
                                .scaleX(1f)
                                .scaleY(1f)
                                .setDuration(150)
                                .start()
                        }.start()
                    onFavoriteClick(meal)
                }
            }
        }

        fun updateFavoriteStatus(isFavorite: Boolean) {
            binding.btnFavorite.setColorFilter(if (isFavorite) Color.RED else Color.BLACK)
        }

        fun bind(item: MealResponse.MealResponseItem) {
            binding.tvMealTitle.text = item.strMeal

            if (!item.strSource.isNullOrBlank()) {
                binding.ivChrome.visibility = View.VISIBLE
                binding.ivChrome.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(item.strSource))
                    binding.root.context.startActivity(intent)
                }
            } else {
                binding.ivChrome.visibility = View.INVISIBLE
            }

            item.strCategory?.let {
                binding.tvCategory.text = it
                binding.ivCategoryIcon.visibility = View.VISIBLE
                binding.tvCategory.visibility = View.VISIBLE
            } ?: run {
                binding.ivCategoryIcon.visibility = View.GONE
                binding.tvCategory.visibility = View.GONE
            }
            item.strArea?.let {
                binding.tvArea.text = it
                binding.ivAreaIcon.visibility = View.VISIBLE
                binding.tvArea.visibility = View.VISIBLE
            } ?: run {
                binding.ivAreaIcon.visibility = View.GONE
                binding.tvArea.visibility = View.GONE
            }
            Glide.with(binding.root.context)
                .load(item.strMealThumb)
                .into(binding.ivMealImage)

            updateFavoriteStatus(favoriteMealIds.contains(item.idMeal))
        }
    }

    companion object {
        private const val PAYLOAD_FAVORITE_STATUS_CHANGED = "PAYLOAD_FAVORITE_STATUS_CHANGED"
    }
}