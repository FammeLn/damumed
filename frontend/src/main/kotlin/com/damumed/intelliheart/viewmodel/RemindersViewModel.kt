package com.damumed.intelliheart.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damumed.intelliheart.network.RetrofitClient
import com.damumed.intelliheart.network.dto.CreateReminderRequest
import com.damumed.intelliheart.network.dto.ReminderResponse
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class RemindersState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val reminders: List<ReminderResponse> = emptyList()
)

class RemindersViewModel : ViewModel() {
    private val api = RetrofitClient.getApiService()
    private val _state = mutableStateOf(RemindersState())
    val state: State<RemindersState> = _state

    fun loadReminders(patientId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val reminders = api.getReminders(patientId)
                _state.value = _state.value.copy(isLoading = false, reminders = reminders)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Еске салуларды жүктеу мүмкін болмады"
                )
            }
        }
    }

    fun createReminder(
        patientId: Long,
        title: String,
        scheduledAt: LocalDateTime,
        note: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                api.createReminder(
                    CreateReminderRequest(
                        patientId = patientId,
                        title = title,
                        scheduledAt = scheduledAt,
                        note = note
                    )
                )
                loadReminders(patientId)
                onSuccess()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.localizedMessage ?: "Еске салуды сақтау мүмкін болмады"
                )
            }
        }
    }

    fun markDone(reminderId: Long, patientId: Long) {
        viewModelScope.launch {
            try {
                api.markReminderDone(reminderId)
                loadReminders(patientId)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.localizedMessage ?: "Еске салуды жаңарту мүмкін болмады"
                )
            }
        }
    }
}
