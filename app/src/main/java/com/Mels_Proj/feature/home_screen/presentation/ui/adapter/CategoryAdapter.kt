package com.Mels_Proj.feature.home_screen.presentation.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.Mels_Proj.databinding.ItemHomeCatBinding
import com.Mels_Proj.feature.category.domain.data.model.CategoriesResponse

class CategoryAdapter(
    private val onItemClick: (CategoriesResponse.CategoryResponseItem) -> Unit
) : RecyclerView.Adapter<CategoryAdapter.ViewHolder>() {

    private var items: List<CategoriesResponse.CategoryResponseItem> = emptyList()
    private var selectedPosition = -1

    fun updateData(newItems: List<CategoriesResponse.CategoryResponseItem>) {
        items = newItems
        selectedPosition = -1
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemHomeCatBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val data = items[position]
        holder.bind(data)
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(private val binding: ItemHomeCatBinding) :
        RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                val clickedPosition = adapterPosition
                if (clickedPosition != RecyclerView.NO_POSITION) {
                    val previousPosition = selectedPosition

                    if (selectedPosition == clickedPosition) {
                        selectedPosition = -1
                        notifyItemChanged(previousPosition)
                    } else {
                        selectedPosition = clickedPosition
                        if (previousPosition != -1) {
                            notifyItemChanged(previousPosition)
                        }
                        notifyItemChanged(selectedPosition)
                    }
                    onItemClick(items[clickedPosition])
                }
            }
        }

        fun bind(item: CategoriesResponse.CategoryResponseItem) {
            binding.tvCategoryName.text = item.strCategory

            Glide.with(binding.root.context)
                .load(item.strCategoryThumb)
                .into(binding.ivCategoryImage)

            val cardView = binding.root
            if (adapterPosition == selectedPosition) {
                val strokeWidthPx = (3 * binding.root.context.resources.displayMetrics.density).toInt()
                cardView.strokeWidth = strokeWidthPx
            } else {
                cardView.strokeWidth = 0
            }
        }
    }
}