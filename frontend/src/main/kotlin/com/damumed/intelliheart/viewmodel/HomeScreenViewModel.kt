package com.damumed.intelliheart.viewmodel

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.damumed.intelliheart.network.RetrofitClient
import com.damumed.intelliheart.network.dto.AssistantRequest
import com.damumed.intelliheart.network.dto.AssistantResponse
import kotlinx.coroutines.launch

/**
 * Состояние для диалога с голосовым помощником
 */
data class AssistantMessage(
    // Текст сообщения
    val text: String,

    // Отправитель (USER или ASSISTANT)
    val sender: String, // "USER" или "ASSISTANT"

    // Время сообщения (для будущей реализации)
    val timestamp: Long = System.currentTimeMillis()
)

/**
 * Состояние для главного экрана
 */
data class HomeScreenState(
    // История сообщений в диалоге
    val messages: List<AssistantMessage> = emptyList(),

    // Распознанный текст (если есть)
    val recognizedText: String = "",

    // Загружается ли ответ от помощника
    val isLoading: Boolean = false,

    // Последнее действие, рекомендованное помощником
    val lastAction: String = "NONE",

    // Ошибка, если возникла
    val error: String? = null
)

/**
 * ViewModel для главного экрана (HomeScreen)
 * Управляет состоянием диалога с голосовым помощником и интегрирует микрофон, TTS и API
 */
class HomeScreenViewModel : ViewModel() {
    private val _screenState = mutableStateOf(HomeScreenState())
    val screenState: State<HomeScreenState> = _screenState

    private val apiService = RetrofitClient.getApiService()

    /**
     * Обработать распознанный текст от микрофона
     * Отправить на сервер и получить ответ от AI помощника
     *
     * @param recognizedText распознанный текст
     */
    fun processRecognizedText(recognizedText: String) {
        // Добавляем сообщение пользователя в историю
        val userMessage = AssistantMessage(
            text = recognizedText,
            sender = "USER"
        )

        // Обновляем состояние с сообщением пользователя
        _screenState.value = _screenState.value.copy(
            messages = _screenState.value.messages + userMessage,
            recognizedText = recognizedText,
            isLoading = true,
            error = null
        )

        // Отправляем запрос на сервер в фоновом режиме
        viewModelScope.launch {
            try {
                // Создаем запрос для API
                val request = AssistantRequest(text = recognizedText)

                // Отправляем запрос к голосовому помощнику
                val response = apiService.queryAssistant(request)

                // Добавляем ответ помощника в историю
                val assistantMessage = AssistantMessage(
                    text = response.text,
                    sender = "ASSISTANT"
                )

                // Обновляем состояние с ответом помощника
                _screenState.value = _screenState.value.copy(
                    messages = _screenState.value.messages + assistantMessage,
                    isLoading = false,
                    lastAction = response.action,
                    error = null
                )
            } catch (e: Exception) {
                // Обработка ошибки
                val errorMessage = "Деректерді жүктеу мүмкін болмады: ${e.message}"

                _screenState.value = _screenState.value.copy(
                    isLoading = false,
                    error = errorMessage
                )
            }
        }
    }

    /**
     * Очистить историю сообщений
     */
    fun clearMessages() {
        _screenState.value = _screenState.value.copy(
            messages = emptyList(),
            recognizedText = "",
            lastAction = "NONE",
            error = null
        )
    }

    /**
     * Установить последнее действие
     */
    fun setLastAction(action: String) {
        _screenState.value = _screenState.value.copy(lastAction = action)
    }
}
