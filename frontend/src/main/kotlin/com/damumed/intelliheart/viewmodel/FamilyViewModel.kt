package com.damumed.intelliheart.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damumed.intelliheart.network.RetrofitClient
import com.damumed.intelliheart.network.dto.CreatePatientRequest
import com.damumed.intelliheart.network.dto.PatientResponse
import kotlinx.coroutines.launch
import java.time.LocalDate

data class FamilyScreenState(
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val error: String? = null,
    val familyMembers: List<PatientResponse> = emptyList(),
    val showAddDialog: Boolean = false
)

class FamilyViewModel : ViewModel() {
    private val _state = mutableStateOf(FamilyScreenState())
    val state: State<FamilyScreenState> = _state

    fun loadFamilyMembers(primaryPatientId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val api = RetrofitClient.getApiService()
                val members = api.getFamilyMembers(primaryPatientId)
                _state.value = _state.value.copy(isLoading = false, familyMembers = members)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Қате шықты"
                )
            }
        }
    }

    fun addFamilyMember(
        primaryPatientId: Long,
        iin: String,
        fullName: String,
        dateOfBirth: LocalDate,
        gender: String,
        phoneNumber: String,
        address: String,
        relationType: String,
        medicalHistory: String?,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, error = null)
            try {
                val api = RetrofitClient.getApiService()
                api.addFamilyMember(
                    primaryPatientId = primaryPatientId,
                    CreatePatientRequest(
                        iin = iin,
                        fullName = fullName,
                        dateOfBirth = dateOfBirth,
                        gender = gender,
                        phoneNumber = phoneNumber,
                        address = address,
                        medicalHistory = medicalHistory?.takeIf { it.isNotBlank() },
                        primaryPatientId = primaryPatientId,
                        relationType = relationType
                    )
                )
                // Перезагружаем список
                loadFamilyMembers(primaryPatientId)
                _state.value = _state.value.copy(isSaving = false, showAddDialog = false)
                onSuccess()
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSaving = false,
                    error = e.localizedMessage ?: "Сақтау қатесі"
                )
            }
        }
    }

    fun createPrimaryPatient(
        iin: String,
        fullName: String,
        dateOfBirth: LocalDate,
        gender: String,
        phoneNumber: String,
        address: String,
        medicalHistory: String?,
        onSuccess: (PatientResponse) -> Unit
    ) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isSaving = true, error = null)
            try {
                val api = RetrofitClient.getApiService()
                val response = api.createPatient(
                    CreatePatientRequest(
                        iin = iin,
                        fullName = fullName,
                        dateOfBirth = dateOfBirth,
                        gender = gender,
                        phoneNumber = phoneNumber,
                        address = address,
                        medicalHistory = medicalHistory?.takeIf { it.isNotBlank() }
                    )
                )
                _state.value = _state.value.copy(isSaving = false)
                onSuccess(response)
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isSaving = false,
                    error = e.localizedMessage ?: "Сақтау қатесі"
                )
            }
        }
    }

    fun showAddDialog() { _state.value = _state.value.copy(showAddDialog = true) }
    fun hideAddDialog() { _state.value = _state.value.copy(showAddDialog = false, error = null) }
}
