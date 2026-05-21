package com.damumed.intelliheart.network

import com.damumed.intelliheart.network.dto.AppointmentResponse
import com.damumed.intelliheart.network.dto.AssistantRequest
import com.damumed.intelliheart.network.dto.AssistantResponse
import com.damumed.intelliheart.network.dto.CreateNotificationRequest
import com.damumed.intelliheart.network.dto.CreateAppointmentRequest
import com.damumed.intelliheart.network.dto.CreatePatientRequest
import com.damumed.intelliheart.network.dto.DoctorResponse
import com.damumed.intelliheart.network.dto.EmailAuthRequest
import com.damumed.intelliheart.network.dto.EmailAuthResponse
import com.damumed.intelliheart.network.dto.NotificationResponse
import com.damumed.intelliheart.network.dto.PatientResponse
import com.damumed.intelliheart.network.dto.TelegramAuthStartRequest
import com.damumed.intelliheart.network.dto.TelegramAuthStartResponse
import com.damumed.intelliheart.network.dto.TelegramAuthStatusResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

/**
 * REST API сервис для работы с бэкендом приложения
 * Определяет все доступные API эндпоинты
 */
interface ApiService {

    /**
     * GET /api/doctors
     * Получить список всех активных врачей
     */
    @GET("/api/doctors")
    suspend fun getDoctors(): List<DoctorResponse>

    /**
     * GET /api/doctors/specialization/{specialization}
     * Получить список врачей по специализации
     */
    @GET("/api/doctors/specialization/{specialization}")
    suspend fun getDoctorsBySpecialization(
        @Path("specialization") specialization: String
    ): List<DoctorResponse>

    /**
     * GET /api/doctors/{id}
     * Получить информацию о враче по ID
     */
    @GET("/api/doctors/{id}")
    suspend fun getDoctorById(
        @Path("id") doctorId: Long
    ): DoctorResponse

    /**
     * POST /api/appointments/book
     * Создать новую запись к врачу
     */
    @POST("/api/appointments/book")
    suspend fun createAppointment(
        @Body request: CreateAppointmentRequest
    ): AppointmentResponse

    /**
     * GET /api/appointments/patient/{patientId}
     * Получить историю всех записей пациента
     */
    @GET("/api/appointments/patient/{patientId}")
    suspend fun getPatientAppointments(
        @Path("patientId") patientId: Long
    ): List<AppointmentResponse>

    /**
     * GET /api/appointments/patient/{patientId}/active
     * Получить активные (ожидающие) записи пациента
     */
    @GET("/api/appointments/patient/{patientId}/active")
    suspend fun getPatientActiveAppointments(
        @Path("patientId") patientId: Long
    ): List<AppointmentResponse>

    /**
     * POST /api/assistant/query
     * Отправить запрос голосовому помощнику и получить умный ответ
     */
    @POST("/api/assistant/query")
    suspend fun queryAssistant(
        @Body request: AssistantRequest
    ): AssistantResponse

    @POST("/api/auth/telegram/start")
    suspend fun startTelegramAuth(
        @Body request: TelegramAuthStartRequest
    ): TelegramAuthStartResponse

    @GET("/api/auth/telegram/status/{authRequestId}")
    suspend fun getTelegramAuthStatus(
        @Path("authRequestId") authRequestId: String
    ): TelegramAuthStatusResponse

    @POST("/api/auth/register")
    suspend fun registerWithEmail(
        @Body request: EmailAuthRequest
    ): EmailAuthResponse

    @POST("/api/auth/login")
    suspend fun loginWithEmail(
        @Body request: EmailAuthRequest
    ): EmailAuthResponse

    @GET("/api/notifications")
    suspend fun getNotifications(
        @Query("userId") userId: Long? = null
    ): List<NotificationResponse>

    @POST("/api/notifications")
    suspend fun createNotification(
        @Body request: CreateNotificationRequest
    ): NotificationResponse

    @POST("/api/patients")
    suspend fun createPatient(
        @Body request: CreatePatientRequest
    ): PatientResponse

    @GET("/api/patients/{id}")
    suspend fun getPatient(
        @Path("id") patientId: Long
    ): PatientResponse
}
