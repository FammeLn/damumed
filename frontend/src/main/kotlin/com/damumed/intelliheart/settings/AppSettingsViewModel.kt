package com.damumed.intelliheart.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/**
 * ViewModel настроек приложения.
 * Читает и записывает тему/язык через AppSettings (DataStore).
 * Singleton на уровне приложения — передаётся сверху вниз.
 */
class AppSettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val settings = AppSettings(application)

    /** Текущая тема (false = светлая, true = тёмная) */
    val isDarkTheme: StateFlow<Boolean> = settings.isDarkTheme
        .stateIn(viewModelScope, SharingStarted.Eagerly, false)

    /** Текущий язык (kz / ru) */
    val language: StateFlow<String> = settings.language
        .stateIn(viewModelScope, SharingStarted.Eagerly, AppSettings.LANG_KZ)

    fun setDarkTheme(isDark: Boolean) {
        viewModelScope.launch { settings.setDarkTheme(isDark) }
    }

    fun setLanguage(lang: String) {
        viewModelScope.launch { settings.setLanguage(lang) }
    }
}
