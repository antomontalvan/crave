package com.example.crave.ui.components

import android.widget.ImageView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.crave.R
import com.example.crave.ui.theme.CraveTheme
import com.example.crave.ui.theme.Fraunces
import com.squareup.picasso.Picasso

/**
 * Meal photo. Compose has no built-in image loader, so we reuse Picasso:
 * AndroidView lets us put a classic View (ImageView) inside a Compose screen.
 */
@Composable
fun MealImage(imageUrl: String, modifier: Modifier = Modifier) {
    AndroidView(
        // factory: creates the ImageView ONLY once.
        factory = { context ->
            ImageView(context).apply {
                scaleType = ImageView.ScaleType.CENTER_CROP
                contentDescription = context.getString(R.string.cd_meal_image)
            }
        },
        // update: runs on creation and every time imageUrl changes.
        update = { imageView ->
            Picasso.get()
                .load(imageUrl)
                .placeholder(android.R.drawable.ic_menu_gallery)
                .error(android.R.drawable.ic_menu_report_image)
                .into(imageView)
        },
        // clipToBounds: CENTER_CROP enlarges the photo, so it's cut to the 280dp box instead of covering the title.
        modifier = modifier.clipToBounds()
    )
}

/** Category and origin (small, uppercase) and the meal name in Fraunces. */
@Composable
fun MealTitleSection(name: String, category: String, area: String) {
    // The API sometimes sends an empty origin: we show a default text.
    val unknown = stringResource(R.string.unknown_data)

    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Text(
            text = "${category.ifBlank { unknown }} · ${area.ifBlank { unknown }}".uppercase(),
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontSize = 12.sp,
            letterSpacing = 2.sp
        )
        Text(
            text = name,
            color = MaterialTheme.colorScheme.primary,
            fontFamily = Fraunces,
            fontSize = 28.sp,
            lineHeight = 34.sp
        )
    }
}

/** Single cookbook button: says "Quitar" (remove) if the recipe is saved and "Guardar" (save) if not. */
@Composable
fun SaveButton(isSaved: Boolean, onClick: () -> Unit) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(4.dp),
        colors = ButtonDefaults.buttonColors(
            // Save = terracotta (main action). Remove = brown.
            containerColor = if (isSaved) {
                MaterialTheme.colorScheme.primary
            } else {
                MaterialTheme.colorScheme.secondary
            }
        )
    ) {
        Text(text = stringResource(if (isSaved) R.string.remove_meal else R.string.save_meal))
    }
}

/** Ingredient list with their measures, one per line. */
@Composable
fun IngredientsSection(ingredients: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
        SectionTitle(text = stringResource(R.string.ingredients_title))
        ingredients.forEach { ingredient ->
            Text(text = "•  $ingredient", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

/** Numbered step-by-step instructions. */
@Composable
fun StepsSection(steps: List<String>) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        SectionTitle(text = stringResource(R.string.steps_title))
        steps.forEachIndexed { index, step ->
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text(
                    text = "${index + 1}",
                    color = MaterialTheme.colorScheme.secondary,
                    fontFamily = Fraunces,
                    fontSize = 20.sp
                )
                Text(
                    text = step,
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

/** Section title ("Ingredientes", "Preparación"). Only used in this file. */
@Composable
private fun SectionTitle(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.primary,
        fontFamily = Fraunces,
        fontSize = 20.sp
    )
}

// ---------------- Previews (shown in Android Studio's "Split" / "Design" panel) ----------------

@Preview(showBackground = true, backgroundColor = 0xFFF5F1EA, name = "Título")
@Composable
private fun MealTitleSectionPreview() {
    CraveTheme {
        MealTitleSection(name = "Teriyaki Chicken Casserole", category = "Chicken", area = "Japanese")
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F1EA, name = "Botón: no guardada")
@Composable
private fun SaveButtonNotSavedPreview() {
    CraveTheme {
        SaveButton(isSaved = false, onClick = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F1EA, name = "Botón: guardada")
@Composable
private fun SaveButtonSavedPreview() {
    CraveTheme {
        SaveButton(isSaved = true, onClick = {})
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F1EA, name = "Ingredientes")
@Composable
private fun IngredientsSectionPreview() {
    CraveTheme {
        IngredientsSection(ingredients = listOf("soy sauce — 3/4 cup", "water — 1/2 cup", "brown rice — 3 cups"))
    }
}

@Preview(showBackground = true, backgroundColor = 0xFFF5F1EA, name = "Pasos")
@Composable
private fun StepsSectionPreview() {
    CraveTheme {
        StepsSection(steps = listOf("Preheat oven to 350° F.", "Combine soy sauce, water and sugar.", "Bake 35 minutes."))
    }
}