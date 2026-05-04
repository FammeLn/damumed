package com.damumed.intelliheart

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.damumed.intelliheart.ui.IntelliHeartApp
import com.damumed.intelliheart.ui.theme.IntelliHeartTheme

/**
 * Главная Activity приложения IntelliHeart
 * Инициализирует Jetpack Compose контент и применяет тему приложения
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Включаем edge-to-edge режим для лучшего внешнего вида
        enableEdgeToEdge()

        // Устанавливаем Compose контент
        setContent {
            // Применяем тему приложения
            IntelliHeartTheme {
                // Отображаем основное приложение с навигацией
                IntelliHeartApp()
            }
        }
    }
}
