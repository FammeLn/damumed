package com.damumed.intelliheart.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damumed.intelliheart.network.RetrofitClient
import com.damumed.intelliheart.network.dto.CreateAppointmentRequest
import com.damumed.intelliheart.network.dto.CreatePatientRequest
import com.damumed.intelliheart.network.dto.DoctorResponse
import com.damumed.intelliheart.network.dto.PatientResponse
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime

data class AppointmentBookingState(
    val doctor: DoctorResponse? = null,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null,
    val availableSlots: List<String> = emptyList()
)

class AppointmentBookingViewModel : ViewModel() {
    private val apiService = RetrofitClient.getApiService()

    private val _state = mutableStateOf(AppointmentBookingState())
    val state: State<AppointmentBookingState> = _state

    fun loadDoctor(doctorId: Long) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true, errorMessage = null)
                val doctor = apiService.getDoctorById(doctorId)
                _state.value = _state.value.copy(doctor = doctor, isLoading = false)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Дәрігер деректерін жүктеу мүмкін болмады"
                )
            }
        }
    }

    fun loadSlots(doctorId: Long, date: String) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true, errorMessage = null)
                val slots = apiService.getDoctorSlots(doctorId, date)
                _state.value = _state.value.copy(
                    isLoading = false,
                    availableSlots = slots.map { it.time }
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Уақыт слоттарын жүктеу мүмкін болмады"
                )
            }
        }
    }

    fun createPatient(
        iin: String,
        fullName: String,
        dateOfBirth: String,
        gender: String,
        phoneNumber: String,
        address: String,
        medicalHistory: String?,
        onSuccess: (PatientResponse) -> Unit
    ) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true, errorMessage = null)
                val parsedDate = runCatching { LocalDate.parse(dateOfBirth) }
                    .getOrElse {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = "Туған күннің форматы дұрыс емес (YYYY-MM-DD)"
                        )
                        return@launch
                    }
                val response = apiService.createPatient(
                    CreatePatientRequest(
                        iin = iin,
                        fullName = fullName,
                        dateOfBirth = parsedDate,
                        gender = gender,
                        phoneNumber = phoneNumber,
                        address = address,
                        medicalHistory = medicalHistory?.takeIf { it.isNotBlank() }
                    )
                )
                _state.value = _state.value.copy(isLoading = false)
                onSuccess(response)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Пациентті сақтау мүмкін болмады"
                )
            }
        }
    }

    fun bookAppointment(
        patientId: Long,
        doctorId: Long,
        date: String,
        time: String,
        reason: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            try {
                _state.value = _state.value.copy(isLoading = true, errorMessage = null, successMessage = null)

                val localDate = runCatching { LocalDate.parse(date) }
                    .getOrElse {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = "Күн форматы дұрыс емес (YYYY-MM-DD)"
                        )
                        return@launch
                    }
                val localTime = runCatching { LocalTime.parse(time) }
                    .getOrElse {
                        _state.value = _state.value.copy(
                            isLoading = false,
                            errorMessage = "Уақыт форматы дұрыс емес (HH:MM)"
                        )
                        return@launch
                    }
                val appointmentDateTime = LocalDateTime.of(localDate, localTime)

                apiService.createAppointment(
                    CreateAppointmentRequest(
                        patientId = patientId,
                        doctorId = doctorId,
                        appointmentDateTime = appointmentDateTime,
                        reasonForVisit = reason?.takeIf { it.isNotBlank() }
                    )
                )

                _state.value = _state.value.copy(
                    isLoading = false,
                    successMessage = "Өтінім сәтті жіберілді"
                )
                onSuccess()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Өтінімді жіберу мүмкін болмады"
                )
            }
        }
    }
}
