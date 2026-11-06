package com.example.crave.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

/** Crave colors for Material 3: the same style as the XML screens. */
private val CraveColorScheme = lightColorScheme(
    primary = CraveBrown,
    onPrimary = CraveCream,
    secondary = CraveTerracotta,
    onSecondary = CraveCream,
    background = CraveCream,
    onBackground = CraveBlack,
    surface = CraveSurface,
    onSurface = CraveBlack,
    onSurfaceVariant = CraveGray
)

/**
 * Theme for the Compose part (detail screen).
 * Unlike the template, it uses no dynamic colors or dark mode,
 * so the detail screen matches the list built in XML.
 */
@Composable
fun CraveTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = CraveColorScheme,
        typography = Typography,
        content = content
    )
}