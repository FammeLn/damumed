package com.damumed.intelliheart.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

/**
 * Экран настроек приложения (MVP уровень)
 * Язык, уведомления, тема
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit = {}
) {
    var language by remember { mutableStateOf("Қазақ") }
    var notificationsEnabled by remember { mutableStateOf(true) }
    var theme by remember { mutableStateOf("Сәнді") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Заголовок
        TopAppBar(
            title = {
                Text(
                    text = "Параметрлер",
                    fontWeight = FontWeight.Bold
                )
            },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Артқа"
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = MaterialTheme.colorScheme.primary,
                titleContentColor = MaterialTheme.colorScheme.onPrimary,
                navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
            )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Раздел тилі
            SettingsSectionTitle(title = "Тіл")

            LanguageSelector(
                selectedLanguage = language,
                onLanguageChange = { language = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Раздел уведомлений
            SettingsSectionTitle(title = "Уведомления")

            SettingsToggleItem(
                title = "Ескертулер",
                description = "Қалпына келтіру ескертулерін алу",
                isEnabled = notificationsEnabled,
                onToggle = { notificationsEnabled = it }
            )

            SettingsToggleItem(
                title = "Апартамент ескертулері",
                description = "Ағындық уведомленияларын алу",
                isEnabled = notificationsEnabled,
                onToggle = { notificationsEnabled = it }
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Раздел оформления
            SettingsSectionTitle(title = "Ресімдеу")

            ThemeSelector(
                selectedTheme = theme,
                onThemeChange = { theme = it }
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Информационный раздел
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Туралы",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "IntelliHeart v1.0",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Text(
                        text = "© 2026 Damumed. Барлық құқылар сақталған.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}

/**
 * Заголовок раздела настроек
 */
@Composable
private fun SettingsSectionTitle(title: String) {
    Text(
        text = title,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(vertical = 8.dp)
    )
}

/**
 * Селектор языка
 */
@Composable
private fun LanguageSelector(
    selectedLanguage: String,
    onLanguageChange: (String) -> Unit
) {
    val languages = listOf("Қазақ", "Русский", "English")

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            languages.forEach { lang ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = lang,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    RadioButton(
                        selected = selectedLanguage == lang,
                        onClick = { onLanguageChange(lang) }
                    )
                }

                if (lang != languages.last()) {
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}

/**
 * Переключатель для параметра
 */
@Composable
private fun SettingsToggleItem(
    title: String,
    description: String,
    isEnabled: Boolean,
    onToggle: (Boolean) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Text(
                    text = description,
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Switch(
                checked = isEnabled,
                onCheckedChange = onToggle
            )
        }
    }
}

/**
 * Селектор темы оформления
 */
@Composable
private fun ThemeSelector(
    selectedTheme: String,
    onThemeChange: (String) -> Unit
) {
    val themes = listOf("Сәнді", "Ашық", "Қараңғы")

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    ) {
        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            themes.forEach { theme ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = theme,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    RadioButton(
                        selected = selectedTheme == theme,
                        onClick = { onThemeChange(theme) }
                    )
                }

                if (theme != themes.last()) {
                    Divider(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp)
                    )
                }
            }
        }
    }
}
