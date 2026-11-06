package com.example.crave.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.crave.R
import com.example.crave.data.model.MealDetailModel
import com.example.crave.data.repository.MealRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * ViewModel for the detail screen (Jetpack Compose).
 *
 * Uses StateFlow, which Compose observes with collectAsState().
 * It first looks for the recipe in SQLite (so it works offline); if it isn't saved, it requests it from the API.
 */
class DetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = MealRepository(application)

    // ---------------- Internal state (only the ViewModel modifies it) ----------------
    private val _meal = MutableStateFlow<MealDetailModel?>(null)
    private val _isSaved = MutableStateFlow(false)
    private val _isLoading = MutableStateFlow(false)
    private val _message = MutableStateFlow<String?>(null)

    // ---------------- Public state (read-only for the UI) ----------------
    val meal: StateFlow<MealDetailModel?> = _meal.asStateFlow()
    val isSaved: StateFlow<Boolean> = _isSaved.asStateFlow()
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    /** Full-screen error message. null = show nothing. */
    val message: StateFlow<String?> = _message.asStateFlow()

    /** Loads the recipe. If it's already loaded (e.g. after rotation), it doesn't request it again. */
    fun loadMeal(id: String) {
        if (_meal.value != null) return

        viewModelScope.launch {
            _isLoading.value = true
            _message.value = null
            try {
                // 1) Is it in the cookbook? Show it from SQLite, without internet.
                val savedMeal = repository.getSavedMeal(id)
                if (savedMeal != null) {
                    _meal.value = savedMeal
                    _isSaved.value = true
                } else {
                    // 2) It isn't saved: request it from the API.
                    val remoteMeal = repository.getMealDetail(id)
                    _meal.value = remoteMeal
                    _isSaved.value = false
                    if (remoteMeal == null) _message.value = text(R.string.error_meal_not_found)
                }
            } catch (e: IOException) {
                _message.value = text(R.string.error_connection)
            } catch (e: Exception) {
                _message.value = text(R.string.error_unexpected)
            } finally {
                _isLoading.value = false
            }
        }
    }

    /** Single button: if the recipe is saved it removes it; otherwise it saves it to the cookbook. */
    fun toggleSaved() {
        val meal = _meal.value ?: return

        viewModelScope.launch {
            if (_isSaved.value) {
                if (repository.removeMeal(meal.id)) _isSaved.value = false
            } else {
                if (repository.saveMeal(meal)) _isSaved.value = true
            }
        }
    }

    private fun text(resId: Int): String = getApplication<Application>().getString(resId)
}