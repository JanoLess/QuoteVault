package com.example.quotevault.presentation.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LatteCream = Color(0xFFFAF6F0)
private val EspressoBrown = Color(0xFF4E342E)
private val MochaBrown = Color(0xFF8D6E63)
private val Terracotta = Color(0xFFB66A50)

private val LightScheme = lightColorScheme(
    primary = Terracotta,
    onPrimary = Color.White,
    secondary = MochaBrown,
    onSecondary = Color.White,
    background = LatteCream,
    onBackground = EspressoBrown,
    surface = LatteCream,
    onSurface = EspressoBrown,
    surfaceVariant = Color(0xFFEDE3D8),
    onSurfaceVariant = MochaBrown,
)

private val DarkScheme = darkColorScheme(
    primary = Terracotta,
    onPrimary = Color.White,
    secondary = Color(0xFFD7CCC8),
    onSecondary = EspressoBrown,
    background = Color(0xFF1C1412),
    onBackground = Color(0xFFF5EDE6),
    surface = Color(0xFF2A211E),
    onSurface = Color(0xFFF5EDE6),
    surfaceVariant = Color(0xFF3E322E),
    onSurfaceVariant = Color(0xFFD7CCC8),
)

@Composable
fun QuoteVaultTheme(
    darkTheme: Boolean,
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkScheme else LightScheme,
        content = content,
    )
}
