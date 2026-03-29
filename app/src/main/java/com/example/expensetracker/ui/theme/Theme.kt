package com.example.expensetracker.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColors = lightColorScheme(
    primary = Emerald,
    secondary = BlueAccent,
    tertiary = Warm,
    background = SlateLight,
    surface = androidx.compose.ui.graphics.Color.White,
    onPrimary = androidx.compose.ui.graphics.Color.White,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onBackground = Slate,
    onSurface = Slate,
    error = Danger
)

private val DarkColors = darkColorScheme(
    primary = Green80,
    secondary = BlueAccent,
    tertiary = Warm,
    background = SurfaceDark,
    surface = CardDark,
    onPrimary = Slate,
    onSecondary = androidx.compose.ui.graphics.Color.White,
    onBackground = androidx.compose.ui.graphics.Color(0xFFF3F4F6),
    onSurface = androidx.compose.ui.graphics.Color(0xFFF3F4F6),
    error = Color(0xFFFCA5A5)
)

@Composable
fun ExpenseTrackerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = AppTypography,
        content = content
    )
}
