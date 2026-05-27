package com.damumed.intelliheart.network

import com.google.gson.GsonBuilder
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.concurrent.TimeUnit

/**
 * Синглтон для создания и управления Retrofit клиентом
 * Обеспечивает единое подключение к бэкенд API
 */
object RetrofitClient {
    // Для физического устройства по USB:
    // 1) adb reverse tcp:8080 tcp:8080
    // 2) оставьте BASE_URL = "http://127.0.0.1:8080/"
    const val BASE_URL = "http://127.0.0.1:8080/"

    // Для эмулятора Android раскомментируйте строку ниже и закомментируйте строку выше.
    // Эмулятор использует 10.0.2.2 для localhost хоста.
    // private const val BASE_URL = "http://10.0.2.2:8080/"

    // Экземпляр Retrofit клиента (ленивая инициализация)
    private val retrofit: Retrofit by lazy {
        // Создаем OkHttpClient с интерцепторами логирования
        val httpClient = OkHttpClient.Builder()
            // Добавляем logging интерцептор для отладки
            .addInterceptor(
                HttpLoggingInterceptor().apply {
                    level = HttpLoggingInterceptor.Level.BODY
                }
            )
            // Устанавливаем таймауты
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()

        val gson = GsonBuilder()
            .registerTypeAdapter(LocalDate::class.java, LocalDateAdapter())
            .registerTypeAdapter(LocalDateTime::class.java, LocalDateTimeAdapter())
            .create()

        // Создаем Retrofit с OkHttpClient и Gson конвертером
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }

    /**
     * Получить API сервис для выполнения запросов
     */
    fun getApiService(): ApiService {
        return retrofit.create(ApiService::class.java)
    }

    fun getBaseUrl(): String = BASE_URL
}
