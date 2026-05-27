package com.damumed.intelliheart.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damumed.intelliheart.network.RetrofitClient
import com.damumed.intelliheart.network.dto.MedicalRecordResponse
import kotlinx.coroutines.launch

data class MedicalRecordState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val records: List<MedicalRecordResponse> = emptyList()
)

class MedicalRecordViewModel : ViewModel() {
    private val _state = mutableStateOf(MedicalRecordState())
    val state: State<MedicalRecordState> = _state

    fun loadRecords(patientId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val api = RetrofitClient.getApiService()
                val records = api.getMedicalRecords(patientId)
                _state.value = _state.value.copy(isLoading = false, records = records)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Медкартаны жүктеу мүмкін болмады"
                )
            }
        }
    }
}
