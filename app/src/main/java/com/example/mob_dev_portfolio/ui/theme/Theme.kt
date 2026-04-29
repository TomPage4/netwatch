package com.example.mob_dev_portfolio.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable

private val AppColorScheme = darkColorScheme(
    // Accent (your pink outlines + primary action)
    primary = Pink,
    onPrimary = White,

    // "Other buttons need to be white"
    secondary = White,
    onSecondary = Black,

    // Top banner needs to be black
    tertiary = Black,
    onTertiary = White,

    // App background + default text
    background = GreyDark,
    onBackground = White,

    // Card backgrounds + text on cards
    surface = GreyMid,
    onSurface = White,

    // "Card outlines need to be pink"
    outline = Pink,

    // "Lines separating items in list need to be greylight"
    outlineVariant = GreyLight,

    // Optional: if you ever use variants (chips, filled tonal buttons, etc.)
    surfaceVariant = GreyMid,
    onSurfaceVariant = GreyLight
)

@Composable
fun MobdevportfolioTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = AppColorScheme,
        typography = Typography,
        content = content
    )
}