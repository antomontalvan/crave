package com.example.crave.data.model

import com.google.gson.annotations.SerializedName

/**
 * Response of filter.php (by category or ingredient) and search.php (by name).
 * Example: { "meals": [ { "idMeal": "52772", "strMeal": "...", "strMealThumb": "..." } ] }
 *
 * When there are no results the API returns { "meals": null }, so the list is nullable.
 */
data class MealResponse(
    @SerializedName("meals") val meals: List<MealModel>?
)

/**
 * A meal as shown in the list: id, name and thumbnail.
 * filter.php only returns these three fields; search.php returns more, but Gson ignores the ones we don't declare.
 */
data class MealModel(
    @SerializedName("idMeal") val id: String,
    @SerializedName("strMeal") val name: String,
    @SerializedName("strMealThumb") val thumbnail: String
)

/** Response of categories.php: { "categories": [ { "strCategory": "Beef", ... } ] } */
data class CategoryResponse(
    @SerializedName("categories") val categories: List<CategoryModel>?
)

/** A category. We only use the name to fill the Spinner. */
data class CategoryModel(
    @SerializedName("strCategory") val name: String
)

/**
 * Response of lookup.php (detail of ONE recipe).
 * The API sends each recipe as an object with many loose fields
 * (strIngredient1..20, strMeasure1..20), so we read it as a Map: key → value.
 */
data class MealDetailResponse(
    @SerializedName("meals") val meals: List<Map<String, String?>>?
)

/**
 * Full recipe, already organized for use in the app.
 * It's the one shown in the detail screen and the one saved in SQLite (offline cookbook).
 */
data class MealDetailModel(
    val id: String,
    val name: String,
    val thumbnail: String,
    val category: String,
    val area: String,
    val instructions: String,
    val ingredients: List<String>
){
    /** Instructions come as a single text: they're split into steps at each line break. */
    val steps: List<String>
        get() = instructions.split("\n").map { it.trim() }.filter { it.isNotBlank() }
}