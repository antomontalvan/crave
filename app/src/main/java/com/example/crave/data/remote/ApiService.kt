package com.example.crave.data.remote

import com.example.crave.data.model.CategoryResponse
import com.example.crave.data.model.MealDetailResponse
import com.example.crave.data.model.MealResponse
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * TheMealDB endpoints used by the app.
 * Retrofit generates this interface's implementation at runtime.
 */
interface ApiService {

    /** GET categories.php -> all categories (for the Spinner) */
    @GET("categories.php")
    suspend fun getCategories(): CategoryResponse

    /** GET filter.php?c={category} -> meals in a category */
    @GET("filter.php")
    suspend fun getMealsByCategory(@Query("c") category: String): MealResponse

    /** GET search.php?s={name} -> meals whose name contains the text */
    @GET("search.php")
    suspend fun searchMealsByName(@Query("s") name: String): MealResponse

    /** GET filter.php?i={ingredient} -> meals that contain that ingredient */
    @GET("filter.php")
    suspend fun searchMealsByIngredient(@Query("i") ingredient: String): MealResponse

    /** Full detail of a recipe by its id. E.g.: lookup.php?i=52772 */
    @GET("lookup.php")
    suspend fun getMealDetail(@Query("i") id: String): MealDetailResponse
}