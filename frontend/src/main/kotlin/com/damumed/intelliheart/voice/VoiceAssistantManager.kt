package com.damumed.intelliheart.voice

import android.content.Context
import android.content.Intent
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import java.util.Locale

/**
 * Менеджер для управления голосовым помощником
 * Использует встроенный в Android SpeechRecognizer для распознавания речи
 */
class VoiceAssistantManager(private val context: Context) {

    // Флаг, слушаем ли мы голос в данный момент
    private var isListening = false

    // Callback для обработки результатов распознавания
    private var onResultCallback: ((String) -> Unit)? = null
    private var onErrorCallback: ((String) -> Unit)? = null

    /**
     * Установить callback для получения результатов распознавания
     */
    fun setOnResultCallback(callback: (String) -> Unit) {
        onResultCallback = callback
    }

    /**
     * Установить callback для обработки ошибок
     */
    fun setOnErrorCallback(callback: (String) -> Unit) {
        onErrorCallback = callback
    }

    /**
     * Начать слушать голос пользователя
     * Поддерживает казахский и русский языки
     */
    fun startListening() {
        // Проверяем, доступен ли SpeechRecognizer на устройстве
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            onErrorCallback?.invoke("Сипатау бөлімі қосымша құрамаса болмайды")
            return
        }

        isListening = true

        // Создаем Intent для SpeechRecognizer
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            // Указываем, что используем стандартный язык модель
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )

            // Устанавливаем язык распознавания (казахский)
            // Также поддерживается русский (ru_RU)
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                "kk_KZ" // Казахский
            )

            // Устанавливаем дополнительные языки
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "kk_KZ,ru_RU" // Казахский и русский
            )

            // Включаем режим мультиязычного распознавания
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_WEB_SEARCH
            )

            // Максимальное количество результатов
            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)

            // Добавляем подсказку для пользователя
            putExtra(
                RecognizerIntent.EXTRA_PROMPT,
                "Ваше заболевание опишите речью..."
            )
        }

        // Запускаем распознавание речи (в реальном приложении нужна интеграция с RecognitionListener)
        // Здесь для простоты используем встроенный диалог системы
        try {
            context.startActivity(intent)
        } catch (e: Exception) {
            onErrorCallback?.invoke("Сипатау өндіріңіз: ${e.message}")
            isListening = false
        }
    }

    /**
     * Остановить слушать голос
     */
    fun stopListening() {
        isListening = false
    }

    /**
     * Получить текущий статус слушания
     */
    fun isCurrentlyListening(): Boolean = isListening

    /**
     * Обработать результаты распознавания
     * Вызывается когда SpeechRecognizer вернул результаты
     */
    fun processRecognitionResults(results: ArrayList<String>) {
        if (results.isNotEmpty()) {
            // Берем первый (наиболее точный) результат
            val recognizedText = results[0]
            onResultCallback?.invoke(recognizedText)
        } else {
            onErrorCallback?.invoke("Дыбысты таныту сәтсіз болды. Қайта іс-әрекет жасаңыз")
        }
        isListening = false
    }

    /**
     * Обработать ошибку распознавания
     */
    fun processRecognitionError(error: String) {
        onErrorCallback?.invoke("Қате: $error")
        isListening = false
    }
}
