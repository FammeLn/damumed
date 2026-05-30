package com.damumed.intelliheart.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.navArgument
import androidx.navigation.NavType
import com.damumed.intelliheart.settings.AppSettings
import com.damumed.intelliheart.settings.AppSettingsViewModel
import com.damumed.intelliheart.ui.components.BottomNavigationBar
import com.damumed.intelliheart.ui.auth.AuthSession
import com.damumed.intelliheart.ui.auth.AuthState
import com.damumed.intelliheart.ui.navigation.Screen
import com.damumed.intelliheart.ui.screens.AppointmentScreen
import com.damumed.intelliheart.ui.screens.AppointmentBookingScreen
import com.damumed.intelliheart.ui.screens.CallDoctorHomeScreen
import com.damumed.intelliheart.ui.screens.HomeScreenMain
import com.damumed.intelliheart.ui.screens.LoginScreen
import com.damumed.intelliheart.ui.screens.MedicalRecordScreen
import com.damumed.intelliheart.ui.screens.NotificationsScreen
import com.damumed.intelliheart.ui.screens.ProfileScreen
import com.damumed.intelliheart.ui.screens.RegisterScreen
import com.damumed.intelliheart.ui.screens.FamilyMembersScreen
import com.damumed.intelliheart.ui.screens.AnalysesScreen
import com.damumed.intelliheart.ui.screens.RemindersScreen
import com.damumed.intelliheart.ui.screens.ChatSupportScreen
import com.damumed.intelliheart.ui.screens.SettingsScreen
import com.damumed.intelliheart.ui.theme.IntelliHeartTheme

/**
 * Основной компонент приложения с навигацией.
 * Содержит NavHost для управления экранами и BottomNavigationBar для переключения вкладок.
 * Настройки темы и языка хранятся в AppSettingsViewModel (DataStore) и применяются глобально.
 */
@Composable
fun IntelliHeartApp() {
    val settingsVm: AppSettingsViewModel = viewModel()
    val isDark by settingsVm.isDarkTheme.collectAsState()
    val language by settingsVm.language.collectAsState()

    IntelliHeartTheme(useDarkTheme = isDark) {
        IntelliHeartAppContent(
            isDark = isDark,
            language = language,
            onSetDark = { settingsVm.setDarkTheme(it) },
            onSetLanguage = { settingsVm.setLanguage(it) }
        )
    }
}

@Composable
private fun IntelliHeartAppContent(
    isDark: Boolean,
    language: String,
    onSetDark: (Boolean) -> Unit,
    onSetLanguage: (String) -> Unit
) {
    val authState = remember { mutableStateOf(AuthState()) }

    if (!authState.value.isAuthenticated) {
        val navController = rememberNavController()
        NavHost(
            navController = navController,
            startDestination = Screen.LoginScreen.route
        ) {
            composable(Screen.LoginScreen.route) {
                LoginScreen(
                    onAuthSuccess = { session: AuthSession ->
                        authState.value = AuthState(
                            isAuthenticated = true,
                            session = session
                        )
                    },
                    onNavigateToRegister = {
                        navController.navigate(Screen.RegisterScreen.route)
                    }
                )
            }

            composable(Screen.RegisterScreen.route) {
                RegisterScreen(
                    onAuthSuccess = { session: AuthSession ->
                        authState.value = AuthState(
                            isAuthenticated = true,
                            session = session
                        )
                    },
                    onNavigateToLogin = {
                        navController.popBackStack()
                    }
                )
            }
        }
    } else {
        val navController = rememberNavController()

        // Отслеживаем активный экран из стека навигации
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: Screen.HomeScreen.route

        // Вкладки нижней панели навигации
        val bottomTabRoutes = listOf(
            Screen.HomeScreen.route,
            Screen.AppointmentScreen.route,
            Screen.MedicalRecordScreen.route,
            Screen.NotificationsScreen.route,
            Screen.ProfileScreen.route
        )

        // Отображаем нижнюю панель только на основных вкладках
        val showBottomBar = currentRoute in bottomTabRoutes

        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    BottomNavigationBar(
                        currentRoute = currentRoute,
                        onNavigate = { screen ->
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                restoreState = true
                                launchSingleTop = true
                            }
                        }
                    )
                }
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.HomeScreen.route
                ) {
                    composable(Screen.HomeScreen.route) {
                        HomeScreenMain(
                            language = language,
                            onNavigateToAppointments = {
                                navController.navigate(Screen.AppointmentScreen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    restoreState = true
                                }
                            },
                            onNavigateToCallDoctor = {
                                navController.navigate(Screen.CallDoctorHomeScreen.route)
                            },
                            onNavigateToRecords = {
                                navController.navigate(Screen.MedicalRecordScreen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    restoreState = true
                                }
                            },
                            onNavigateToProfile = {
                                navController.navigate(Screen.ProfileScreen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    restoreState = true
                                }
                            },
                            onNavigateToAnalyses = {
                                navController.navigate(Screen.AnalysesScreen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    restoreState = true
                                }
                            },
                            onNavigateToChat = {
                                navController.navigate(Screen.ChatSupportScreen.route)
                            }
                        )
                    }

                    composable(Screen.AppointmentScreen.route) {
                        AppointmentScreen(
                            onBookDoctor = { doctorId ->
                                navController.navigate(Screen.AppointmentBookingScreen.createRoute(doctorId))
                            },
                            session = authState.value.session
                        )
                    }

                    composable(
                        route = Screen.AppointmentBookingScreen.route,
                        arguments = listOf(navArgument("doctorId") { type = NavType.LongType })
                    ) { backStackEntry ->
                        val doctorId = backStackEntry.arguments?.getLong("doctorId") ?: 0L
                        AppointmentBookingScreen(
                            doctorId = doctorId,
                            session = authState.value.session,
                            onPatientCreated = { patientId ->
                                val currentSession = authState.value.session
                                if (currentSession != null) {
                                    authState.value = AuthState(
                                        isAuthenticated = true,
                                        session = currentSession.copy(patientId = patientId)
                                    )
                                }
                            },
                            onBookingSuccess = {
                                navController.popBackStack()
                            },
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(Screen.MedicalRecordScreen.route) {
                        MedicalRecordScreen(
                            session = authState.value.session,
                            onNavigateToAnalyses = {
                                navController.navigate(Screen.AnalysesScreen.route)
                            },
                            onNavigateToReminders = {
                                navController.navigate(Screen.RemindersScreen.route)
                            }
                        )
                    }

                    composable(Screen.NotificationsScreen.route) {
                        NotificationsScreen(session = authState.value.session)
                    }

                    composable(Screen.AnalysesScreen.route) {
                        AnalysesScreen(
                            session = authState.value.session,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.RemindersScreen.route) {
                        RemindersScreen(
                            session = authState.value.session,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(Screen.ProfileScreen.route) {
                        ProfileScreen(
                            session = authState.value.session,
                            onNavigateToFamily = {
                                navController.navigate(Screen.FamilyMembersScreen.route)
                            },
                            onNavigateToChat = {
                                navController.navigate(Screen.ChatSupportScreen.route)
                            },
                            onNavigateToSettings = {
                                navController.navigate(Screen.SettingsScreen.route)
                            },
                            onLogout = {
                                authState.value = AuthState()
                            }
                        )
                    }

                    composable(Screen.FamilyMembersScreen.route) {
                        FamilyMembersScreen(
                            session = authState.value.session,
                            onBack = {
                                navController.popBackStack()
                            },
                            onPrimaryCreated = { patientId ->
                                val currentSession = authState.value.session
                                if (currentSession != null) {
                                    authState.value = AuthState(
                                        isAuthenticated = true,
                                        session = currentSession.copy(patientId = patientId)
                                    )
                                }
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(Screen.CallDoctorHomeScreen.route) {
                        CallDoctorHomeScreen(
                            session = authState.value.session,
                            onBack = {
                                navController.popBackStack()
                            },
                            onSuccess = {
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(Screen.ChatSupportScreen.route) {
                        ChatSupportScreen(
                            session = authState.value.session,
                            onBack = {
                                navController.popBackStack()
                            },
                            onNavigateToAppointments = {
                                navController.navigate(Screen.AppointmentScreen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    restoreState = true
                                }
                            },
                            onNavigateToCallDoctor = {
                                navController.navigate(Screen.CallDoctorHomeScreen.route)
                            },
                            onNavigateToRecords = {
                                navController.navigate(Screen.MedicalRecordScreen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    restoreState = true
                                }
                            },
                            onNavigateToProfile = {
                                navController.navigate(Screen.ProfileScreen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    restoreState = true
                                }
                            }
                        )
                    }

                    composable(Screen.SettingsScreen.route) {
                        SettingsScreen(
                            isDark = isDark,
                            language = language,
                            onSetDark = onSetDark,
                            onSetLanguage = onSetLanguage,
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}
