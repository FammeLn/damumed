package com.damumed.intelliheart.settings

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "damumed_settings")

/**
 * Хранилище настроек приложения через DataStore.
 * Настройки сохраняются между запусками.
 */
class AppSettings(private val context: Context) {

    companion object {
        val KEY_DARK_THEME = booleanPreferencesKey("dark_theme")
        val KEY_LANGUAGE = stringPreferencesKey("language")
        const val LANG_KZ = "kz"
        const val LANG_RU = "ru"
    }

    /** Поток текущей темы (true = тёмная) */
    val isDarkTheme: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[KEY_DARK_THEME] ?: false
    }

    /** Поток текущего языка (kz / ru) */
    val language: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[KEY_LANGUAGE] ?: LANG_KZ
    }

    /** Сохранить тему */
    suspend fun setDarkTheme(isDark: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[KEY_DARK_THEME] = isDark
        }
    }

    /** Сохранить язык */
    suspend fun setLanguage(lang: String) {
        context.dataStore.edit { prefs ->
            prefs[KEY_LANGUAGE] = lang
        }
    }
}
