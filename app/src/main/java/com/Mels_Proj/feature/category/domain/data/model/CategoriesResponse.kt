package com.Mels_Proj.feature.category.domain.data.model
import com.google.gson.annotations.SerializedName

class CategoriesResponse(@SerializedName("categories") val categoryList: List<CategoryResponseItem>) {
    data class CategoryResponseItem(
        val idCategory: String?,
        val strCategory: String?,
        val strCategoryThumb: String?,
        val strCategoryDescription: String?
    )
}