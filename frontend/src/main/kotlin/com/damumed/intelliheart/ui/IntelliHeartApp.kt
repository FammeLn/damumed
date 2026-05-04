package com.damumed.intelliheart.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.damumed.intelliheart.ui.components.BottomNavigationBar
import com.damumed.intelliheart.ui.navigation.Screen
import com.damumed.intelliheart.ui.screens.AppointmentScreen
import com.damumed.intelliheart.ui.screens.HomeScreen
import com.damumed.intelliheart.ui.screens.MedicalRecordScreen
import com.damumed.intelliheart.ui.screens.ProfileScreen

/**
 * Основной компонент приложения с навигацией
 * Содержит NavHost для управления экранами и BottomNavigationBar для переключения вкладок
 */
@Composable
fun IntelliHeartApp() {
    // Создаем контроллер навигации
    val navController = rememberNavController()

    // Отслеживаем текущий маршрут для выделения активной вкладки
    val currentRoute = remember { mutableStateOf(Screen.HomeScreen.route) }

    Scaffold(
        bottomBar = {
            // Отображаем нижнюю навигационную панель
            BottomNavigationBar(
                currentRoute = currentRoute.value,
                onNavigate = { screen ->
                    // Обновляем текущий маршрут и переходим на экран
                    currentRoute.value = screen.route
                    navController.navigate(screen.route) {
                        // Избегаем создания множественных копий экранов в back stack
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        lazyRestoreState = true
                        // Не разрешаем несколько копий одного экрана
                        restoreState = true
                    }
                }
            )
        }
    ) { innerPadding ->
        // Основной контент - NavHost для переключения между экранами
        Box(modifier = Modifier.padding(innerPadding)) {
            NavHost(
                navController = navController,
                startDestination = Screen.HomeScreen.route
            ) {
                // Главный экран
                composable(Screen.HomeScreen.route) {
                    HomeScreen()
                }

                // Экран записи к врачу - используем новый экран с врачами
                composable(Screen.AppointmentScreen.route) {
                    AppointmentScreen()
                }

                // Экран медицинской карты
                composable(Screen.MedicalRecordScreen.route) {
                    MedicalRecordScreen()
                }

                // Экран профиля
                composable(Screen.ProfileScreen.route) {
                    ProfileScreen()
                }
            }
        }
    }
}
