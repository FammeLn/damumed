package com.damumed.intelliheart.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

/**
 * Основные цвета приложения (Премиум Палитра)
 */
object IntelliHeartColors {
    // Основной цвет (синий сапфир)
    val Primary = Color(0xFF0F62FE)
    val PrimaryLight = Color(0xFF78A9FF)
    val PrimaryDark = Color(0xFF002D9C)

    // Вторичный цвет (зелёный клевер)
    val Secondary = Color(0xFF008A52)
    val SecondaryLight = Color(0xFF4CFFB4)
    val SecondaryDark = Color(0xFF004D2C)

    // Третичный цвет (оранжевый закат)
    val Tertiary = Color(0xFFFF8300)
    val TertiaryLight = Color(0xFFFFAA47)
    val TertiaryDark = Color(0xFFB35C00)

    // Серые тона
    val Background = Color(0xFFF4F6FA)
    val Surface = Color(0xFFFFFFFF)
    val Error = Color(0xFFDA1E28)

    // Глобальные градиенты
    val PrimaryGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF0F62FE), Color(0xFF3399FF))
    )
    val SecondaryGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF008A52), Color(0xFF00AA66))
    )
    val AccentGradient = Brush.linearGradient(
        colors = listOf(Color(0xFF0F62FE), Color(0xFF008A52))
    )
    val CardGradientLight = Brush.linearGradient(
        colors = listOf(Color(0xFFFFFFFF), Color(0xFFF0F4FA))
    )
    val CardGradientDark = Brush.linearGradient(
        colors = listOf(Color(0xFF1E2638), Color(0xFF131824))
    )
}

/**
 * Светлая палитра цветов
 */
private val LightColorScheme = lightColorScheme(
    primary = IntelliHeartColors.Primary,
    secondary = IntelliHeartColors.Secondary,
    tertiary = IntelliHeartColors.Tertiary,
    background = IntelliHeartColors.Background,
    surface = IntelliHeartColors.Surface,
    error = IntelliHeartColors.Error,
    primaryContainer = Color(0xFFEDF5FF),
    onPrimaryContainer = Color(0xFF002D9C),
    secondaryContainer = Color(0xFFE5F6EE),
    onSecondaryContainer = Color(0xFF003E21),
    surfaceVariant = Color(0xFFE0E6F0),
    onSurfaceVariant = Color(0xFF475266)
)

/**
 * Тёмная палитра цветов (Slate Navy)
 */
private val DarkColorScheme = darkColorScheme(
    primary = IntelliHeartColors.PrimaryLight,
    secondary = IntelliHeartColors.SecondaryLight,
    tertiary = IntelliHeartColors.TertiaryLight,
    background = Color(0xFF121620),
    surface = Color(0xFF1A2130),
    error = Color(0xFFFF6B6B),
    primaryContainer = Color(0xFF002D9C),
    onPrimaryContainer = Color(0xFFEDF5FF),
    secondaryContainer = Color(0xFF004D2C),
    onSecondaryContainer = Color(0xFFE5F6EE),
    surfaceVariant = Color(0xFF2C354A),
    onSurfaceVariant = Color(0xFFBAC3D4),
    onBackground = Color(0xFFF4F6FA),
    onSurface = Color(0xFFF4F6FA)
)

/**
 * Тема приложения IntelliHeart
 */
@Composable
fun IntelliHeartTheme(
    useDarkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        useDarkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content
    )
}
