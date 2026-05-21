package com.damumed.intelliheart.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damumed.intelliheart.network.RetrofitClient
import com.damumed.intelliheart.network.dto.EmailAuthRequest
import com.damumed.intelliheart.ui.auth.AuthSession
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class AuthViewModel : ViewModel() {
    private val apiService = RetrofitClient.getApiService()

    private val _uiState = mutableStateOf(AuthUiState())
    val uiState: State<AuthUiState> = _uiState

    fun login(email: String, password: String, onSuccess: (AuthSession) -> Unit) {
        viewModelScope.launch {
            try {
                _uiState.value = AuthUiState(isLoading = true)

                val response = apiService.loginWithEmail(
                    EmailAuthRequest(email = email, password = password)
                )

                _uiState.value = AuthUiState()
                onSuccess(
                    AuthSession(
                        userId = response.userId,
                        email = response.email,
                        accessToken = response.accessToken,
                        expiresAt = response.expiresAt,
                        patientId = null
                    )
                )
            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    isLoading = false,
                    errorMessage = e.message ?: "Кіру мүмкін болмады"
                )
            }
        }
    }

    fun register(email: String, password: String, onSuccess: (AuthSession) -> Unit) {
        viewModelScope.launch {
            try {
                _uiState.value = AuthUiState(isLoading = true)

                val response = apiService.registerWithEmail(
                    EmailAuthRequest(email = email, password = password)
                )

                _uiState.value = AuthUiState()
                onSuccess(
                    AuthSession(
                        userId = response.userId,
                        email = response.email,
                        accessToken = response.accessToken,
                        expiresAt = response.expiresAt,
                        patientId = null
                    )
                )
            } catch (e: Exception) {
                _uiState.value = AuthUiState(
                    isLoading = false,
                    errorMessage = e.message ?: "Тіркелу мүмкін болмады"
                )
            }
        }
    }
}
