package com.damumed.intelliheart.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damumed.intelliheart.network.RetrofitClient
import com.damumed.intelliheart.network.dto.CreateNotificationRequest
import com.damumed.intelliheart.network.dto.NotificationResponse
import kotlinx.coroutines.launch

sealed class NotificationsUiState {
    object Loading : NotificationsUiState()
    data class Success(val notifications: List<NotificationResponse>) : NotificationsUiState()
    data class Error(val message: String) : NotificationsUiState()
}

class NotificationsViewModel : ViewModel() {
    private val apiService = RetrofitClient.getApiService()

    private val _uiState = mutableStateOf<NotificationsUiState>(NotificationsUiState.Loading)
    val uiState: State<NotificationsUiState> = _uiState

    fun loadNotifications(userId: Long?) {
        viewModelScope.launch {
            try {
                _uiState.value = NotificationsUiState.Loading
                val notifications = apiService.getNotifications(userId)
                _uiState.value = NotificationsUiState.Success(notifications)
            } catch (e: Exception) {
                _uiState.value = NotificationsUiState.Error(
                    e.message ?: "Уведомленияны жүктеу мүмкін болмады"
                )
            }
        }
    }

    fun createNotification(
        userId: Long?,
        title: String,
        message: String,
        onCreated: (NotificationResponse) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val response = apiService.createNotification(
                    CreateNotificationRequest(
                        title = title,
                        message = message,
                        userId = userId
                    )
                )

                val current = (_uiState.value as? NotificationsUiState.Success)?.notifications.orEmpty()
                _uiState.value = NotificationsUiState.Success(listOf(response) + current)
                onCreated(response)
            } catch (e: Exception) {
                _uiState.value = NotificationsUiState.Error(
                    e.message ?: "Уведомление құру мүмкін болмады"
                )
            }
        }
    }
}
