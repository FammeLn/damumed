package com.damumed.intelliheart.voice

import android.os.Bundle
import android.content.Context
import android.speech.RecognizerIntent
import android.speech.RecognitionListener
import android.speech.SpeechRecognizer
import java.util.Locale

/**
 * Менеджер для управления голосовым помощником
 * Использует встроенный в Android SpeechRecognizer для распознавания речи
 */
class VoiceAssistantManager(private val context: Context) {
    private val appContext = context.applicationContext
    private val speechRecognizer: SpeechRecognizer? = if (SpeechRecognizer.isRecognitionAvailable(appContext)) {
        SpeechRecognizer.createSpeechRecognizer(appContext).also { recognizer ->
            recognizer.setRecognitionListener(createRecognitionListener())
        }
    } else {
        null
    }

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
        if (speechRecognizer == null) {
            onErrorCallback?.invoke("Бұл құрылғыда дауыс тану қызметі қолжетімсіз")
            return
        }
        if (isListening) return

        // Создаем Intent для SpeechRecognizer
        val intent = android.content.Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            // Указываем, что используем стандартный язык модель
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
            )

            // Устанавливаем язык распознавания (казахский)
            // Также поддерживается русский (ru_RU)
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE,
                "kk-KZ" // Казахский
            )

            // Устанавливаем дополнительные языки
            putExtra(
                RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
                "kk-KZ" // Приоритет казахского
            )
            putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, arrayListOf("kk-KZ", "ru-RU"))

            putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Сұрағыңызды айтыңыз...")
        }

        try {
            isListening = true
            speechRecognizer.startListening(intent)
        } catch (e: Exception) {
            onErrorCallback?.invoke("Дауыс тануды іске қосу сәтсіз: ${e.message}")
            isListening = false
        }
    }

    /**
     * Остановить слушать голос
     */
    fun stopListening() {
        if (isListening) {
            speechRecognizer?.stopListening()
        }
        isListening = false
    }

    fun destroy() {
        speechRecognizer?.destroy()
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
    private fun processRecognitionResults(results: ArrayList<String>?) {
        val recognizedText = results?.firstOrNull()
        if (recognizedText.isNullOrBlank()) {
            onErrorCallback?.invoke("Дыбысты тану сәтсіз болды. Қайталап көріңіз")
            return
        }
        onResultCallback?.invoke(recognizedText)
    }

    private fun createRecognitionListener(): RecognitionListener {
        return object : RecognitionListener {
            override fun onReadyForSpeech(params: Bundle?) = Unit
            override fun onBeginningOfSpeech() = Unit
            override fun onRmsChanged(rmsdB: Float) = Unit
            override fun onBufferReceived(buffer: ByteArray?) = Unit
            override fun onEndOfSpeech() = Unit
            override fun onPartialResults(partialResults: Bundle?) = Unit
            override fun onEvent(eventType: Int, params: Bundle?) = Unit

            override fun onResults(results: Bundle?) {
                val spoken = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                processRecognitionResults(spoken)
                isListening = false
            }

            override fun onError(error: Int) {
                onErrorCallback?.invoke(mapError(error))
                isListening = false
            }
        }
    }

    private fun mapError(error: Int): String {
        return when (error) {
            SpeechRecognizer.ERROR_AUDIO -> "Микрофоннан аудио оқу сәтсіз болды"
            SpeechRecognizer.ERROR_CLIENT -> "Клиенттік қате. Қайта байқап көріңіз"
            SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> "Микрофонға рұқсат беріңіз"
            SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> "Желі қателігі. Интернетті тексеріңіз"
            SpeechRecognizer.ERROR_NO_MATCH -> "Сөз танылмады. Қайта айтып көріңіз"
            SpeechRecognizer.ERROR_RECOGNIZER_BUSY -> "Дауыс тану қазір бос емес"
            SpeechRecognizer.ERROR_SERVER -> "Дауыс тану сервері жауап бермеді"
            SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> "Сөйлеу уақыты бітті"
            else -> "Дауыс тануда белгісіз қате пайда болды"
        }
    }
}
