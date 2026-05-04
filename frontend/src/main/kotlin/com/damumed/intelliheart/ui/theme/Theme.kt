package com.damumed.intelliheart.ui.theme

import androidx.compose.foundation.isSystemInDarkMode
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/**
 * Основные цвета приложения
 */
object IntelliHeartColors {
    // Основной цвет (синий - цвет здоровья)
    val Primary = Color(0xFF0066CC)
    val PrimaryLight = Color(0xFF3399FF)
    val PrimaryDark = Color(0xFF004499)

    // Вторичный цвет (зелёный - цвет благополучия)
    val Secondary = Color(0xFF00AA66)
    val SecondaryLight = Color(0xFF33CC99)
    val SecondaryDark = Color(0xFF007744)

    // Третичный цвет (оранжевый - цвет предупреждений)
    val Tertiary = Color(0xFFFF8800)
    val TertiaryLight = Color(0xFFFFAA33)
    val TertiaryDark = Color(0xFFCC6600)

    // Серые тона
    val Background = Color(0xFFFAFAFA)
    val Surface = Color(0xFFFFFFFF)
    val Error = Color(0xFFCC0000)
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
    error = IntelliHeartColors.Error
)

/**
 * Тёмная палитра цветов
 */
private val DarkColorScheme = darkColorScheme(
    primary = IntelliHeartColors.PrimaryLight,
    secondary = IntelliHeartColors.SecondaryLight,
    tertiary = IntelliHeartColors.TertiaryLight,
    background = Color(0xFF121212),
    surface = Color(0xFF1E1E1E),
    error = Color(0xFFFF6666)
)

/**
 * Тема приложения IntelliHeart
 * Применяет светлую или тёмную палитру в зависимости от системных настроек устройства
 */
@Composable
fun IntelliHeartTheme(
    useDarkTheme: Boolean = isSystemInDarkMode(),
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
