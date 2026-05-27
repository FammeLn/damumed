package com.damumed.intelliheart.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damumed.intelliheart.network.RetrofitClient
import com.damumed.intelliheart.network.dto.AppointmentResponse
import com.damumed.intelliheart.network.dto.RescheduleAppointmentRequest
import kotlinx.coroutines.launch
import java.time.LocalDateTime

data class AppointmentsState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val appointments: List<AppointmentResponse> = emptyList()
)

class AppointmentsViewModel : ViewModel() {
    private val api = RetrofitClient.getApiService()
    private val _state = mutableStateOf(AppointmentsState())
    val state: State<AppointmentsState> = _state

    fun loadAppointments(patientId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val list = api.getPatientAppointments(patientId)
                _state.value = _state.value.copy(isLoading = false, appointments = list)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Жазылуларды жүктеу мүмкін болмады"
                )
            }
        }
    }

    fun cancelAppointment(appointmentId: Long, patientId: Long) {
        viewModelScope.launch {
            try {
                api.cancelAppointment(appointmentId)
                loadAppointments(patientId)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.localizedMessage ?: "Жазылуды болдырмау мүмкін болмады"
                )
            }
        }
    }

    fun rescheduleAppointment(
        appointmentId: Long,
        patientId: Long,
        newDateTime: LocalDateTime
    ) {
        viewModelScope.launch {
            try {
                api.rescheduleAppointment(
                    appointmentId,
                    RescheduleAppointmentRequest(newDateTime)
                )
                loadAppointments(patientId)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.localizedMessage ?: "Жазылуды ауыстыру мүмкін болмады"
                )
            }
        }
    }
}
