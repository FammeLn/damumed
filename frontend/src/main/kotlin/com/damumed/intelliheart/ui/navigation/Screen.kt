package com.damumed.intelliheart.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.LocalHospital
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Chat
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
        displayName = "Дәрігерге жазылу",
        icon = Icons.Default.Menu
    )

    object AppointmentBookingScreen : Screen(
        route = "appointment_booking/{doctorId}",
        displayName = "Жазылу",
        icon = Icons.Default.Menu
    ) {
        fun createRoute(doctorId: Long): String = "appointment_booking/$doctorId"
    }

    // Экран медицинской карты
    object MedicalRecordScreen : Screen(
        route = "medical_record",
        displayName = "Медициналық карта",
        icon = Icons.Default.HealthAndSafety
    )

    // Экран профиля
    object ProfileScreen : Screen(
        route = "profile",
        displayName = "Жеке кабинет",
        icon = Icons.Default.Person
    )

    object NotificationsScreen : Screen(
        route = "notifications",
        displayName = "Уведомления",
        icon = Icons.Default.Notifications
    )

    object FamilyMembersScreen : Screen(
        route = "family_members",
        displayName = "Отбасы",
        icon = Icons.Default.Person
    )

    // Экран анализов
    object AnalysesScreen : Screen(
        route = "analyses",
        displayName = "Анализдер",
        icon = Icons.Default.Description
    )

    object RemindersScreen : Screen(
        route = "reminders",
        displayName = "Еске салу",
        icon = Icons.Default.Description
    )

    // Экран вызова врача на дом (без навигации в BottomNav)
    object CallDoctorHomeScreen : Screen(
        route = "call_doctor_home",
        displayName = "Дәрігерді үйге шақыру",
        icon = Icons.Default.LocalHospital
    )

    // Экран авторизации
    object LoginScreen : Screen(
        route = "login",
        displayName = "Кіру",
        icon = Icons.Default.Person
    )

    // Экран регистрации
    object RegisterScreen : Screen(
        route = "register",
        displayName = "Тіркелу",
        icon = Icons.Default.Person
    )

    // Экран чата поддержки
    object ChatSupportScreen : Screen(
        route = "chat_support",
        displayName = "Қолдау",
        icon = Icons.Default.Chat
    )

    // Экран настроек
    object SettingsScreen : Screen(
        route = "settings",
        displayName = "Параметрлер",
        icon = Icons.Default.Settings
    )

    companion object {
        /**
         * Получить список всех экранов нижней навигации
         */
        fun getBottomNavigationItems(): List<Screen> = listOf(
            HomeScreen,
            AppointmentScreen,
            MedicalRecordScreen,
            NotificationsScreen,
            ProfileScreen
        )
    }
}
