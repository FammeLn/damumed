package com.damumed.intelliheart.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * Запечатанный класс для типобезопасной навигации между экранами
 * Каждый экран имеет маршрут, казахское отображаемое имя и иконку
 */
sealed class Screen(
    val route: String,
    val displayName: String,
    val icon: ImageVector
) {
    // Главный экран
    object HomeScreen : Screen(
        route = "home",
        displayName = "Басты бет",
        icon = Icons.Default.Home
    )

    // Экран записи к врачу
    object AppointmentScreen : Screen(
        route = "appointment",
        displayName = "Жазылу",
        icon = Icons.Default.Menu
    )

    // Экран медицинской карты
    object MedicalRecordScreen : Screen(
        route = "medical_record",
        displayName = "Медкарта",
        icon = Icons.Default.HealthAndSafety
    )

    // Экран профиля
    object ProfileScreen : Screen(
        route = "profile",
        displayName = "Профиль",
        icon = Icons.Default.Person
    )

    // Экран вызова врача на дом (без навигации в BottomNav)
    object CallDoctorHomeScreen : Screen(
        route = "call_doctor_home",
        displayName = "Дәрігерді үйге шақыру",
        icon = Icons.Default.LocalHospital
    )

    companion object {
        /**
         * Получить список всех экранов нижней навигации
         */
        fun getBottomNavigationItems(): List<Screen> = listOf(
            HomeScreen,
            AppointmentScreen,
            MedicalRecordScreen,
            ProfileScreen
        )
    }
}
