package com.damumed.intelliheart.network

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

/**
 * Синглтон для создания и управления Retrofit клиентом
 * Обеспечивает единое подключение к бэкенд API
 */
object RetrofitClient {
    // URL базового адреса бэкенда (измените на реальный адрес сервера)
    private const val BASE_URL = "http://192.168.1.100:8080"

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

        // Создаем Retrofit с OkHttpClient и Gson конвертером
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(httpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    /**
     * Получить API сервис для выполнения запросов
     */
    fun getApiService(): ApiService {
        return retrofit.create(ApiService::class.java)
    }
}
