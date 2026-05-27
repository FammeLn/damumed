package com.damumed.intelliheart.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damumed.intelliheart.network.RetrofitClient
import com.damumed.intelliheart.network.dto.CreateHomeDoctorRequest
import com.damumed.intelliheart.network.dto.HomeDoctorResponse
import kotlinx.coroutines.launch

data class HomeDoctorState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val requests: List<HomeDoctorResponse> = emptyList()
)

class HomeDoctorViewModel : ViewModel() {
    private val api = RetrofitClient.getApiService()
    private val _state = mutableStateOf(HomeDoctorState())
    val state: State<HomeDoctorState> = _state

    fun loadRequests(patientId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val list = api.getHomeDoctorRequests(patientId)
                _state.value = _state.value.copy(isLoading = false, requests = list)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Өтінімдерді жүктеу мүмкін болмады"
                )
            }
        }
    }

    fun createRequest(patientId: Long, symptoms: List<String>, address: String, onSuccess: () -> Unit) {
        viewModelScope.launch {
            try {
                api.createHomeDoctorRequest(
                    CreateHomeDoctorRequest(
                        patientId = patientId,
                        symptoms = symptoms,
                        address = address
                    )
                )
                loadRequests(patientId)
                onSuccess()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    error = e.localizedMessage ?: "Өтінімді жіберу мүмкін болмады"
                )
            }
        }
    }
}
