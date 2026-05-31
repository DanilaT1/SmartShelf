package com.example.bookworm.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.example.bookworm.model.AppAppearance
import com.example.bookworm.model.AppColorChoice

private val LightColors = lightColorScheme()
private val DarkColors = darkColorScheme()

@Composable
fun BookWormTheme(
    appearance: AppAppearance = AppAppearance(),
    content: @Composable () -> Unit,
) {
    val isDark = isSystemInDarkTheme()
    val base = if (isDark) DarkColors else LightColors
    val backgroundColor = appearance.backgroundColor.toBackgroundColor(isDark) ?: base.background
    val fontColor = appearance.fontColor.toFontColor(isDark) ?: base.onBackground
    val colorScheme = base.copy(
        background = backgroundColor,
        surface = backgroundColor,
        surfaceContainer = backgroundColor,
        onBackground = fontColor,
        onSurface = fontColor,
    )

    MaterialTheme(
        colorScheme = colorScheme,
        typography = MaterialTheme.typography,
        content = content,
    )
}

private fun AppColorChoice.toBackgroundColor(isDark: Boolean): Color? = when (this) {
    AppColorChoice.SYSTEM -> null
    AppColorChoice.CREAM -> Color(0xFFFFF8E7)
    AppColorChoice.MINT -> Color(0xFFE8F5E9)
    AppColorChoice.LAVENDER -> Color(0xFFF1E7FF)
    AppColorChoice.CHARCOAL -> Color(0xFF202124)
    AppColorChoice.WHITE -> Color.White
    AppColorChoice.BLACK -> Color.Black
    AppColorChoice.BLUE -> Color(0xFFE3F2FD)
    AppColorChoice.GREEN -> Color(0xFFE8F5E9)
    AppColorChoice.PURPLE -> Color(0xFFF3E5F5)
    AppColorChoice.BROWN -> Color(0xFFEFEBE9)
}

private fun AppColorChoice.toFontColor(isDark: Boolean): Color? = when (this) {
    AppColorChoice.SYSTEM -> null
    AppColorChoice.CREAM -> Color(0xFFFFF8E7)
    AppColorChoice.MINT -> Color(0xFF1B5E20)
    AppColorChoice.LAVENDER -> Color(0xFF4A148C)
    AppColorChoice.CHARCOAL -> Color(0xFF202124)
    AppColorChoice.WHITE -> Color.White
    AppColorChoice.BLACK -> Color.Black
    AppColorChoice.BLUE -> Color(0xFF0D47A1)
    AppColorChoice.GREEN -> Color(0xFF1B5E20)
    AppColorChoice.PURPLE -> Color(0xFF4A148C)
    AppColorChoice.BROWN -> Color(0xFF4E342E)
}
