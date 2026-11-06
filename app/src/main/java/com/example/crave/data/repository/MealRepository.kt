package com.example.crave.data.repository

import android.content.Context
import com.example.crave.data.local.MealDbHelper
import com.example.crave.data.model.CategoryModel
import com.example.crave.data.model.MealDetailModel
import com.example.crave.data.model.MealModel
import com.example.crave.data.remote.ApiService
import com.example.crave.data.remote.RetrofitClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext


/**
 * Repository pattern: the ONLY entry point to the data for the ViewModels.
 *
 * The ViewModel doesn't know where the data comes from: internet (Retrofit) or the local database (SQLite).
 * All operations run on Dispatchers.IO so they don't block the UI.
 */
class MealRepository(context: Context) {
    private val api: ApiService = RetrofitClient.apiService
    private val dbHelper = MealDbHelper(context.applicationContext)

    // ---------- Remote (API) ----------

    /** Categories for the Spinner. If the API returns null, an empty list is returned. */
    suspend fun getCategories(): List<CategoryModel> =
        withContext(Dispatchers.IO) { api.getCategories().categories.orEmpty() }

    /** Meals in a category (e.g. "Beef"). */
    suspend fun getMealsByCategory(category: String): List<MealModel> =
        withContext(Dispatchers.IO) { api.getMealsByCategory(category).meals.orEmpty() }

    /**
     * Combined search: returns the meals whose NAME contains the text
     * and the meals that contain that INGREDIENT, without duplicates.
     */
    suspend fun searchMeals(query: String): List<MealModel> =
        withContext(Dispatchers.IO) {
            val byName = api.searchMealsByName(query).meals.orEmpty()
            val byIngredient = api.searchMealsByIngredient(query).meals.orEmpty()
            (byName + byIngredient).distinctBy { it.id }
        }

    /** Fetches the full recipe from the API. Returns null if the API doesn't find it. */
    suspend fun getMealDetail(id: String): MealDetailModel? =
        withContext(Dispatchers.IO) {
            val meal = api.getMealDetail(id).meals?.firstOrNull()
            meal?.let { toMealDetail(it) }
        }

    /** Converts the API's "raw" recipe (Map of loose fields) into our model. */
    private fun toMealDetail(meal: Map<String, String?>): MealDetailModel {
        // Gather the 20 ingredient/measure pairs, skipping the empty ones ("" or null).
        val ingredients = mutableListOf<String>()
        for (i in 1..20) {
            val ingredient = meal["strIngredient$i"]?.trim().orEmpty()
            val measure = meal["strMeasure$i"]?.trim().orEmpty()
            if (ingredient.isNotEmpty()) {
                ingredients.add(if (measure.isEmpty()) ingredient else "$ingredient — $measure")
            }
        }

        return MealDetailModel(
            id = meal["idMeal"].orEmpty(),
            name = meal["strMeal"].orEmpty(),
            thumbnail = meal["strMealThumb"].orEmpty(),
            category = meal["strCategory"].orEmpty(),
            area = meal["strArea"].orEmpty(),
            instructions = meal["strInstructions"].orEmpty(),
            ingredients = ingredients
        )
    }


    // ---------- Local (SQLite) ----------

    /** Recipes saved in the cookbook (works without internet). */
    suspend fun getSavedMeals(): List<MealModel> =
        withContext(Dispatchers.IO) { dbHelper.getAllMeals() }

    /** Full saved recipe, or null if it isn't in the cookbook. */
    suspend fun getSavedMeal(id: String): MealDetailModel? =
        withContext(Dispatchers.IO) { dbHelper.getMeal(id) }

    suspend fun saveMeal(meal: MealDetailModel): Boolean =
        withContext(Dispatchers.IO) { dbHelper.insertMeal(meal) != -1L }

    suspend fun removeMeal(id: String): Boolean =
        withContext(Dispatchers.IO) { dbHelper.deleteMeal(id) > 0 }
}   