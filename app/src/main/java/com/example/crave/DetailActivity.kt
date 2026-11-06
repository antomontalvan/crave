package com.example.crave

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.crave.ui.screen.MealDetailScreen
import com.example.crave.ui.theme.CraveTheme
import com.example.crave.ui.viewmodel.DetailViewModel

/**
 * Activity for the detail screen, built with Jetpack Compose.
 * Receives the recipe id through the Intent and passes it to the DetailViewModel.
 */
class DetailActivity : ComponentActivity() {

    companion object {
        // Key of the value carried in the Intent (both Fragments use it to open this screen).
        const val EXTRA_MEAL_ID = "extra_meal_id"
    }

    private val viewModel: DetailViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Without an id there's nothing to show: close the screen.
        val mealId = intent.getStringExtra(EXTRA_MEAL_ID)
        if (mealId == null) {
            finish()
            return
        }
        viewModel.loadMeal(mealId)

        setContent {
            CraveTheme {
                MealDetailScreen(viewModel = viewModel, onBack = { finish() })
            }
        }
    }
}