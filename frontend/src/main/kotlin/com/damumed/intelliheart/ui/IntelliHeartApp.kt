package com.damumed.intelliheart.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import androidx.navigation.NavType
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

/**
 * Основной компонент приложения с навигацией
 * Содержит NavHost для управления экранами и BottomNavigationBar для переключения вкладок
 */
@Composable
fun IntelliHeartApp() {
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
        val currentRoute = remember { mutableStateOf(Screen.HomeScreen.route) }

        Scaffold(
            bottomBar = {
                if (currentRoute.value != Screen.CallDoctorHomeScreen.route) {
                    BottomNavigationBar(
                        currentRoute = currentRoute.value,
                        onNavigate = { screen ->
                            currentRoute.value = screen.route
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.startDestinationId) {
                                    saveState = true
                                }
                                restoreState = true
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
                            onNavigateToAppointments = {
                                currentRoute.value = Screen.AppointmentScreen.route
                                navController.navigate(Screen.AppointmentScreen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    restoreState = true
                                }
                            },
                            onNavigateToCallDoctor = {
                                currentRoute.value = Screen.CallDoctorHomeScreen.route
                                navController.navigate(Screen.CallDoctorHomeScreen.route)
                            },
                            onNavigateToRecords = {
                                currentRoute.value = Screen.MedicalRecordScreen.route
                                navController.navigate(Screen.MedicalRecordScreen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    restoreState = true
                                }
                            },
                            onNavigateToProfile = {
                                currentRoute.value = Screen.ProfileScreen.route
                                navController.navigate(Screen.ProfileScreen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    restoreState = true
                                }
                            },
                            onNavigateToAnalyses = {
                                currentRoute.value = Screen.AnalysesScreen.route
                                navController.navigate(Screen.AnalysesScreen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    restoreState = true
                                }
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
                            onSuccess = {
                                currentRoute.value = Screen.HomeScreen.route
                                navController.popBackStack()
                            }
                        )
                    }

                    composable(Screen.ChatSupportScreen.route) {
                        ChatSupportScreen(
                            onBack = {
                                navController.popBackStack()
                            },
                            onNavigateToAppointments = {
                                currentRoute.value = Screen.AppointmentScreen.route
                                navController.navigate(Screen.AppointmentScreen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    restoreState = true
                                }
                            },
                            onNavigateToCallDoctor = {
                                currentRoute.value = Screen.CallDoctorHomeScreen.route
                                navController.navigate(Screen.CallDoctorHomeScreen.route)
                            },
                            onNavigateToRecords = {
                                currentRoute.value = Screen.MedicalRecordScreen.route
                                navController.navigate(Screen.MedicalRecordScreen.route) {
                                    popUpTo(navController.graph.startDestinationId) {
                                        saveState = true
                                    }
                                    restoreState = true
                                }
                            },
                            onNavigateToProfile = {
                                currentRoute.value = Screen.ProfileScreen.route
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
