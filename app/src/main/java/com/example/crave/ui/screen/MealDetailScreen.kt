package com.example.crave.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.crave.R
import com.example.crave.data.model.MealDetailModel
import com.example.crave.ui.components.IngredientsSection
import com.example.crave.ui.components.MealImage
import com.example.crave.ui.components.MealTitleSection
import com.example.crave.ui.components.SaveButton
import com.example.crave.ui.components.StepsSection
import com.example.crave.ui.theme.Fraunces
import com.example.crave.ui.viewmodel.DetailViewModel

/**
 * Detail screen. Observes the DetailViewModel's StateFlows and decides what to show:
 * loading, the recipe, or an error message.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealDetailScreen(viewModel: DetailViewModel, onBack: () -> Unit) {
    // collectAsState(): every time the StateFlow changes, Compose redraws this screen.
    val meal by viewModel.meal.collectAsState()
    val isSaved by viewModel.isSaved.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val message by viewModel.message.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = stringResource(R.string.detail_title), fontFamily = Fraunces) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(R.string.cd_back)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    titleContentColor = MaterialTheme.colorScheme.primary,
                    navigationIconContentColor = MaterialTheme.colorScheme.primary
                )
            )
        },
        containerColor = MaterialTheme.colorScheme.background
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding),
            contentAlignment = Alignment.Center
        ) {
            // Local copies: Kotlin can't smart cast properties delegated with "by".
            val currentMeal = meal
            val currentMessage = message

            when {
                isLoading -> CircularProgressIndicator(color = MaterialTheme.colorScheme.primary)
                currentMeal != null -> MealDetailContent(
                    meal = currentMeal,
                    isSaved = isSaved,
                    onToggleSaved = { viewModel.toggleSaved() }
                )
                currentMessage != null -> Text(
                    text = currentMessage,
                    modifier = Modifier.padding(24.dp),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontFamily = Fraunces,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

/** Recipe content: photo on top, then title, button, ingredients and steps below. */
@Composable
fun MealDetailContent(meal: MealDetailModel, isSaved: Boolean, onToggleSaved: () -> Unit) {
    // LazyColumn: vertically scrolling list (instructions can be long).
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = 24.dp)
    ) {
        item {
            MealImage(
                imageUrl = meal.thumbnail,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(280.dp)
            )
        }
        item {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                MealTitleSection(name = meal.name, category = meal.category, area = meal.area)
                SaveButton(isSaved = isSaved, onClick = onToggleSaved)
                IngredientsSection(ingredients = meal.ingredients)
                StepsSection(steps = meal.steps)
            }
        }
    }
}