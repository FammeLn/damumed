package com.damumed.intelliheart.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damumed.intelliheart.network.RetrofitClient
import com.damumed.intelliheart.network.dto.SendMessageRequest
import kotlinx.coroutines.launch
import java.time.ZoneId

/**
 * Состояние для чата поддержки с AI-ботом
 */
data class ChatSupportState(
    val messages: List<AssistantMessage> = emptyList(),
    val isLoading: Boolean = false,
    val lastAction: String = "NONE",
    val error: String? = null
)

/**
 * ViewModel для экрана чата поддержки
 */
class ChatSupportViewModel : ViewModel() {
    private val apiService = RetrofitClient.getApiService()

    private val _state = mutableStateOf(ChatSupportState())
    val state: State<ChatSupportState> = _state

    /**
     * Загрузить историю чата из базы данных
     */
    fun loadHistory(patientId: Long) {
        _state.value = _state.value.copy(isLoading = true, error = null)
        viewModelScope.launch {
            try {
                val history = apiService.getChatHistory(patientId)
                val mappedMessages = history.map { response ->
                    AssistantMessage(
                        text = response.text,
                        sender = if (response.sender == "USER") "USER" else "ASSISTANT",
                        timestamp = response.createdAt.atZone(ZoneId.systemDefault()).toInstant().toEpochMilli()
                    )
                }
                _state.value = _state.value.copy(
                    messages = mappedMessages,
                    isLoading = false,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Тарихты жүктеу мүмкін болмады"
                )
            }
        }
    }

    /**
     * Отправить сообщение и получить ответ
     */
    fun sendMessage(patientId: Long, text: String) {
        val trimmed = text.trim()
        if (trimmed.isEmpty()) {
            _state.value = _state.value.copy(error = "Хабарлама бос болмауы керек")
            return
        }

        val userMessage = AssistantMessage(
            text = trimmed,
            sender = "USER"
        )

        _state.value = _state.value.copy(
            messages = _state.value.messages + userMessage,
            isLoading = true,
            error = null
        )

        viewModelScope.launch {
            try {
                val response = apiService.sendChatMessage(
                    SendMessageRequest(
                        patientId = patientId,
                        text = trimmed
                    )
                )

                val assistantMessage = AssistantMessage(
                    text = response.text,
                    sender = "ASSISTANT"
                )

                _state.value = _state.value.copy(
                    messages = _state.value.messages + assistantMessage,
                    isLoading = false,
                    lastAction = response.action,
                    error = null
                )
            } catch (e: Exception) {
                _state.value = _state.value.copy(
                    isLoading = false,
                    error = e.message ?: "Хабарламаны жіберу мүмкін болмады"
                )
            }
        }
    }

    fun clearAction() {
        _state.value = _state.value.copy(lastAction = "NONE")
    }
}

