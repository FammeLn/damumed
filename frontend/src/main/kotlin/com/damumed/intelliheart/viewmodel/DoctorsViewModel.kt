package com.damumed.intelliheart.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damumed.intelliheart.network.RetrofitClient
import com.damumed.intelliheart.network.dto.DoctorResponse
import kotlinx.coroutines.launch

/**
 * Состояние для отображения списка врачей
 */
sealed class DoctorListUiState {
    // Загрузка данных
    object Loading : DoctorListUiState()

    // Успешно получены врачи
    data class Success(val doctors: List<DoctorResponse>) : DoctorListUiState()

    // Ошибка при загрузке
    data class Error(val message: String) : DoctorListUiState()
}

/**
 * ViewModel для управления состоянием списка врачей
 * Отвечает за загрузку данных врачей с бэкенда и управление состоянием UI
 */
class DoctorsViewModel : ViewModel() {
    private val _uiState = mutableStateOf<DoctorListUiState>(DoctorListUiState.Loading)
    val uiState: State<DoctorListUiState> = _uiState

    private val apiService = RetrofitClient.getApiService()

    init {
        // Загружаем врачей при инициализации ViewModel
        loadDoctors()
    }

    /**
     * Загрузить список всех врачей с бэкенда
     */
    fun loadDoctors() {
        viewModelScope.launch {
            try {
                // Устанавливаем состояние загрузки
                _uiState.value = DoctorListUiState.Loading

                // Выполняем запрос к API
                val doctors = apiService.getDoctors()

                // Устанавливаем успешное состояние с данными
                _uiState.value = DoctorListUiState.Success(doctors)
            } catch (e: Exception) {
                // Обрабатываем ошибку
                _uiState.value = DoctorListUiState.Error(
                    e.message ?: "Деректерді жүктеу мүмкін болмады"
                )
            }
        }
    }

    /**
     * Загрузить врачей по специализации
     */
    fun loadDoctorsBySpecialization(specialization: String) {
        viewModelScope.launch {
            try {
                _uiState.value = DoctorListUiState.Loading

                val doctors = apiService.getDoctorsBySpecialization(specialization)

                _uiState.value = DoctorListUiState.Success(doctors)
            } catch (e: Exception) {
                _uiState.value = DoctorListUiState.Error(
                    e.message ?: "Деректерді жүктеу мүмкін болмады"
                )
            }
        }
    }
}
