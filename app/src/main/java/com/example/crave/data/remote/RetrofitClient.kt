package com.example.crave.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Singleton (object) that builds ONE single Retrofit instance for the whole app.
 * "by lazy" means it's only created the first time someone uses apiService.
 */
object RetrofitClient {

    private const val BASE_URL = "https://www.themealdb.com/api/json/v1/1/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)                                  // All paths start from this URL
            .addConverterFactory(GsonConverterFactory.create()) // JSON -> data classes with Gson
            .build()
            .create(ApiService::class.java)                     // ApiService implementation
    }
}