package com.example.unusuallycomposedlist.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable

private val LightColors = lightColors(
    primary = primaryLight,
    primaryVariant = primaryVariantLight,
    secondary = secondaryLight
)

private val DarkColors = darkColors(
    primary = primaryDark,
    primaryVariant = primaryVariantDark,
    secondary = secondaryDark
)

@Composable
fun AppTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    MaterialTheme(
        colors = if (darkTheme) {
            DarkColors
        } else {
            LightColors
        },
        content = content
    )
}