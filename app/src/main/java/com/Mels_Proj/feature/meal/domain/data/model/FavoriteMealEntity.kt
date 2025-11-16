package com.Mels_Proj.feature.meal.domain.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.Mels_Proj.feature.meal.domain.data.Constants

@Entity(tableName = Constants.FAVORITE_MEALS_TABLE_NAME)
data class FavoriteMealEntity(
    @PrimaryKey
    @ColumnInfo(name = Constants.MEAL_ID_COLUMN)
    val idMeal: String,

    @ColumnInfo(name = Constants.MEAL_NAME_COLUMN)
    val strMeal: String?,

    @ColumnInfo(name = Constants.MEAL_THUMBNAIL_COLUMN)
    val strMealThumb: String?,

    @ColumnInfo(name = Constants.MEAL_CATEGORY_COLUMN)
    val strCategory: String?,

    @ColumnInfo(name = Constants.MEAL_AREA_COLUMN)
    val strArea: String?,

    @ColumnInfo(name = Constants.MEAL_INSTRUCTIONS_COLUMN)
    val strInstructions: String?,

    @ColumnInfo(name = Constants.MEAL_YOUTUBE_LINK_COLUMN)
    val strYoutube: String?,

    @ColumnInfo(name = Constants.MEAL_SOURCE_LINK_COLUMN)
    val strSource: String?,

    @ColumnInfo(name = Constants.MEAL_TAGS_COLUMN)
    val strTags: String?,

    @ColumnInfo(name = Constants.INGREDIENT_1_COLUMN) val strIngredient1: String?,
    @ColumnInfo(name = Constants.INGREDIENT_2_COLUMN) val strIngredient2: String?,
    @ColumnInfo(name = Constants.INGREDIENT_3_COLUMN) val strIngredient3: String?,
    @ColumnInfo(name = Constants.INGREDIENT_4_COLUMN) val strIngredient4: String?,
    @ColumnInfo(name = Constants.INGREDIENT_5_COLUMN) val strIngredient5: String?,
    @ColumnInfo(name = Constants.INGREDIENT_6_COLUMN) val strIngredient6: String?,
    @ColumnInfo(name = Constants.INGREDIENT_7_COLUMN) val strIngredient7: String?,
    @ColumnInfo(name = Constants.INGREDIENT_8_COLUMN) val strIngredient8: String?,
    @ColumnInfo(name = Constants.INGREDIENT_9_COLUMN) val strIngredient9: String?,
    @ColumnInfo(name = Constants.INGREDIENT_10_COLUMN) val strIngredient10: String?,
    @ColumnInfo(name = Constants.INGREDIENT_11_COLUMN) val strIngredient11: String?,
    @ColumnInfo(name = Constants.INGREDIENT_12_COLUMN) val strIngredient12: String?,
    @ColumnInfo(name = Constants.INGREDIENT_13_COLUMN) val strIngredient13: String?,
    @ColumnInfo(name = Constants.INGREDIENT_14_COLUMN) val strIngredient14: String?,
    @ColumnInfo(name = Constants.INGREDIENT_15_COLUMN) val strIngredient15: String?,
    @ColumnInfo(name = Constants.INGREDIENT_16_COLUMN) val strIngredient16: String?,
    @ColumnInfo(name = Constants.INGREDIENT_17_COLUMN) val strIngredient17: String?,
    @ColumnInfo(name = Constants.INGREDIENT_18_COLUMN) val strIngredient18: String?,
    @ColumnInfo(name = Constants.INGREDIENT_19_COLUMN) val strIngredient19: String?,
    @ColumnInfo(name = Constants.INGREDIENT_20_COLUMN) val strIngredient20: String?,

    @ColumnInfo(name = Constants.MEASURE_1_COLUMN) val strMeasure1: String?,
    @ColumnInfo(name = Constants.MEASURE_2_COLUMN) val strMeasure2: String?,
    @ColumnInfo(name = Constants.MEASURE_3_COLUMN) val strMeasure3: String?,
    @ColumnInfo(name = Constants.MEASURE_4_COLUMN) val strMeasure4: String?,
    @ColumnInfo(name = Constants.MEASURE_5_COLUMN) val strMeasure5: String?,
    @ColumnInfo(name = Constants.MEASURE_6_COLUMN) val strMeasure6: String?,
    @ColumnInfo(name = Constants.MEASURE_7_COLUMN) val strMeasure7: String?,
    @ColumnInfo(name = Constants.MEASURE_8_COLUMN) val strMeasure8: String?,
    @ColumnInfo(name = Constants.MEASURE_9_COLUMN) val strMeasure9: String?,
    @ColumnInfo(name = Constants.MEASURE_10_COLUMN) val strMeasure10: String?,
    @ColumnInfo(name = Constants.MEASURE_11_COLUMN) val strMeasure11: String?,
    @ColumnInfo(name = Constants.MEASURE_12_COLUMN) val strMeasure12: String?,
    @ColumnInfo(name = Constants.MEASURE_13_COLUMN) val strMeasure13: String?,
    @ColumnInfo(name = Constants.MEASURE_14_COLUMN) val strMeasure14: String?,
    @ColumnInfo(name = Constants.MEASURE_15_COLUMN) val strMeasure15: String?,
    @ColumnInfo(name = Constants.MEASURE_16_COLUMN) val strMeasure16: String?,
    @ColumnInfo(name = Constants.MEASURE_17_COLUMN) val strMeasure17: String?,
    @ColumnInfo(name = Constants.MEASURE_18_COLUMN) val strMeasure18: String?,
    @ColumnInfo(name = Constants.MEASURE_19_COLUMN) val strMeasure19: String?,
    @ColumnInfo(name = Constants.MEASURE_20_COLUMN) val strMeasure20: String?
)