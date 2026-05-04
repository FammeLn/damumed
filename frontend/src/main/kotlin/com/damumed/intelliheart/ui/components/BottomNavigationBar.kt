package com.damumed.intelliheart.ui.components

import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.damumed.intelliheart.ui.navigation.Screen

/**
 * Компонент нижней навигационной панели
 * Отображает 4 основные вкладки приложения с иконками и названиями на казахском языке
 */
@Composable
fun BottomNavigationBar(
    currentRoute: String,
    modifier: Modifier = Modifier,
    onNavigate: (Screen) -> Unit
) {
    // Получаем список всех экранов для навигации
    val items = Screen.getBottomNavigationItems()

    NavigationBar(
        modifier = modifier
    ) {
        items.forEach { screen ->
            // Определяем, выбран ли текущий экран
            val isSelected = currentRoute == screen.route

            NavigationBarItem(
                icon = {
                    Icon(
                        imageVector = screen.icon,
                        contentDescription = screen.displayName
                    )
                },
                label = {
                    Text(text = screen.displayName)
                },
                selected = isSelected,
                onClick = {
                    // Переход на выбранный экран при клике
                    onNavigate(screen)
                }
            )
        }
    }
}
