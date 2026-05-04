package com.damumed.intelliheart.voice

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.TextToSpeech.OnInitListener
import android.util.Log
import java.util.Locale

/**
 * Менеджер для озвучивания текста с использованием встроенного Android TextToSpeech
 * Поддерживает казахский и русский языки
 */
class TextToSpeechManager(private val context: Context) : OnInitListener {

    // Экземпляр TextToSpeech
    private var textToSpeech: TextToSpeech? = null

    // Флаг инициализации TTS
    private var isInitialized = false

    // Callback для уведомления о завершении озвучивания
    private var onCompleteCallback: (() -> Unit)? = null

    init {
        // Инициализируем TextToSpeech с поддержкой казахского языка
        textToSpeech = TextToSpeech(context, this)
    }

    /**
     * Callback при инициализации TextToSpeech
     */
    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            isInitialized = true

            // Устанавливаем язык казахский, если доступен
            val languageResult = textToSpeech?.setLanguage(Locale("kk", "KZ"))

            // Если казахский не доступен, пробуем русский
            if (languageResult == TextToSpeech.LANG_MISSING_DATA || languageResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                Log.w("TTS", "Казахский язык не доступен, используем русский")
                textToSpeech?.setLanguage(Locale("ru", "RU"))
            }

            // Устанавливаем параметры голоса
            textToSpeech?.apply {
                setPitch(1.0f) // Нормальный тон голоса
                setSpeechRate(0.9f) // Немного медленнее для лучшего восприятия
            }
        } else {
            isInitialized = false
            Log.e("TTS", "Ошибка инициализации TextToSpeech: $status")
        }
    }

    /**
     * Озвучить текст на казахском/русском языке
     * @param text текст для озвучивания
     * @param onComplete callback при завершении озвучивания
     */
    fun speak(text: String, onComplete: (() -> Unit)? = null) {
        if (!isInitialized) {
            Log.w("TTS", "TextToSpeech еще не инициализирован")
            return
        }

        onCompleteCallback = onComplete

        // Установим слушатель для уведомления о завершении
        textToSpeech?.setOnUtteranceProgressListener(object : android.speech.tts.UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {}

            override fun onDone(utteranceId: String?) {
                onCompleteCallback?.invoke()
            }

            override fun onError(utteranceId: String?) {
                Log.e("TTS", "Ошибка при озвучивании")
            }
        })

        // Озвучиваем текст с ID для отслеживания
        textToSpeech?.speak(text, TextToSpeech.QUEUE_FLUSH, null, "assistant_response")
    }

    /**
     * Остановить озвучивание текста
     */
    fun stop() {
        if (isInitialized) {
            textToSpeech?.stop()
        }
    }

    /**
     * Освободить ресурсы TextToSpeech
     */
    fun shutdown() {
        textToSpeech?.stop()
        textToSpeech?.shutdown()
        isInitialized = false
    }

    /**
     * Проверить, инициализирован ли TTS
     */
    fun isReady(): Boolean = isInitialized
}
