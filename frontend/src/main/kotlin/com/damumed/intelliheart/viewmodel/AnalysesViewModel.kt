package com.damumed.intelliheart.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damumed.intelliheart.network.RetrofitClient
import com.damumed.intelliheart.network.dto.AnalysisResponse
import kotlinx.coroutines.launch

data class AnalysesScreenState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val analyses: List<AnalysisResponse> = emptyList()
)

class AnalysesViewModel : ViewModel() {
    private val _state = mutableStateOf(AnalysesScreenState())
    val state: State<AnalysesScreenState> = _state

    fun loadAnalyses(patientId: Long) {
        viewModelScope.launch {
            _state.value = _state.value.copy(isLoading = true, error = null)
            try {
                val api = RetrofitClient.getApiService()
                val result = api.getAnalyses(patientId)
                _state.value = _state.value.copy(
                    isLoading = false,
                    analyses = result
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.localizedMessage ?: "Белгісіз қате"
                )
            }
        }
    }
}
