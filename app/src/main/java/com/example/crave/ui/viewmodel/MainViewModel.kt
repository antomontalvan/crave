package com.example.crave.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.example.crave.R
import com.example.crave.data.model.MealModel
import com.example.crave.data.repository.MealRepository
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * ViewModel for the main screen (meal list by category + search).
 *
 * Holds the state the UI observes and decides WHAT to load (category or search).
 * It doesn't know how the data is obtained: MealRepository takes care of that.
 */
class MainViewModel(application: Application) : AndroidViewModel(application) {

    companion object {
        /** Category shown when the app opens. */
        const val DEFAULT_CATEGORY = "Beef"
    }

    private val repository = MealRepository(application)

    // ---------------- Internal state (private, mutable) ----------------
    private val _categories = MutableLiveData<List<String>>(emptyList())
    private val _meals = MutableLiveData<List<MealModel>>(emptyList())
    private val _isLoading = MutableLiveData(false)
    private val _message = MutableLiveData<String?>(null)

    private val _savedMeals = MutableLiveData<List<MealModel>>(emptyList())

    /** Category selected in the Spinner. Used to go back to it when the search is cleared. */
    var currentCategory = DEFAULT_CATEGORY
        private set

    // ---------------- Public state (read-only for the UI) ----------------
    val categories: LiveData<List<String>> = _categories
    val meals: LiveData<List<MealModel>> = _meals
    val isLoading: LiveData<Boolean> = _isLoading

    /** Centered list message (error or no results). null = show nothing. */
    val message: LiveData<String?> = _message

    /** Recipes saved in SQLite (cookbook screen). */
    val savedMeals: LiveData<List<MealModel>> = _savedMeals

    init {
        loadCategories()
        loadMeals(query = null)
    }

    // ---------------- Events reported by the UI ----------------

    /** The user picked a category in the Spinner. */
    fun selectCategory(category: String) {
        // The Spinner also fires when it loads: if it's the same category, we don't reload.
        if (category == currentCategory) return
        currentCategory = category
        loadMeals(query = null)
    }

    /** The user searched for a text. If it's empty, go back to the selected category. */
    fun searchMeals(query: String) {
        val text = query.trim()
        loadMeals(query = text.ifBlank { null })
    }

    /** The cookbook reports it's being shown: read the saved recipes from SQLite. */
    fun loadSavedMeals() {
        viewModelScope.launch {
            try {
                _savedMeals.value = repository.getSavedMeals()
            } catch (e: Exception) {
                _savedMeals.value = emptyList()
            }
        }
    }

    // ---------------- Private logic ----------------

    private fun loadCategories() {
        viewModelScope.launch {
            try {
                _categories.value = repository.getCategories().map { it.name }
            } catch (e: Exception) {
                // No connection: the Spinner shows at least the default category.
                _categories.value = listOf(DEFAULT_CATEGORY)
            }
        }
    }

    /**
     * Loads the meal list.
     * query == null -> meals of the current category; otherwise -> search by name/ingredient.
     */
    private fun loadMeals(query: String?) {
        viewModelScope.launch {
            _isLoading.value = true
            _message.value = null
            try {
                val result = if (query == null) {
                    repository.getMealsByCategory(currentCategory)
                } else {
                    repository.searchMeals(query)
                }
                _meals.value = result
                _message.value = if (result.isEmpty()) text(R.string.empty_meals) else null
            } catch (e: IOException) {
                _meals.value = emptyList()
                _message.value = text(R.string.error_connection)
            } catch (e: Exception) {
                _meals.value = emptyList()
                _message.value = text(R.string.error_unexpected)
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun text(resId: Int): String = getApplication<Application>().getString(resId)
}