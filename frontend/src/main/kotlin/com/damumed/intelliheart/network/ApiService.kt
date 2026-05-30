package com.damumed.intelliheart.network

import com.damumed.intelliheart.network.dto.AppointmentResponse
import com.damumed.intelliheart.network.dto.AssistantRequest
import com.damumed.intelliheart.network.dto.AssistantResponse
import com.damumed.intelliheart.network.dto.CreateNotificationRequest
import com.damumed.intelliheart.network.dto.CreateAppointmentRequest
import com.damumed.intelliheart.network.dto.CreatePatientRequest
import com.damumed.intelliheart.network.dto.CreateMedicalRecordRequest
import com.damumed.intelliheart.network.dto.CreateAnalysisRequest
import com.damumed.intelliheart.network.dto.CreateReminderRequest
import com.damumed.intelliheart.network.dto.CreateHomeDoctorRequest
import com.damumed.intelliheart.network.dto.DoctorResponse
import com.damumed.intelliheart.network.dto.EmailAuthRequest
import com.damumed.intelliheart.network.dto.EmailAuthResponse
import com.damumed.intelliheart.network.dto.NotificationResponse
import com.damumed.intelliheart.network.dto.PatientResponse
import com.damumed.intelliheart.network.dto.MedicalRecordResponse
import com.damumed.intelliheart.network.dto.AnalysisResponse
import com.damumed.intelliheart.network.dto.ReminderResponse
import com.damumed.intelliheart.network.dto.HomeDoctorResponse
import com.damumed.intelliheart.network.dto.AppointmentSlotResponse
import com.damumed.intelliheart.network.dto.RescheduleAppointmentRequest
import com.damumed.intelliheart.network.dto.TelegramAuthStartRequest
import com.damumed.intelliheart.network.dto.TelegramAuthStartResponse
import com.damumed.intelliheart.network.dto.TelegramAuthStatusResponse
import com.damumed.intelliheart.network.dto.ChatMessageResponse
import com.damumed.intelliheart.network.dto.SendMessageRequest
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.DELETE
import retrofit2.http.Path
import retrofit2.http.Query
import retrofit2.http.PUT
import okhttp3.MultipartBody
import retrofit2.http.Multipart
import retrofit2.http.Part

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

    @DELETE("/api/appointments/{appointmentId}")
    suspend fun cancelAppointment(
        @Path("appointmentId") appointmentId: Long
    ): Map<String, String>

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

    @GET("/api/patients/{id}/family")
    suspend fun getFamilyMembers(
        @Path("id") primaryPatientId: Long
    ): List<PatientResponse>

    @POST("/api/patients/{id}/family")
    suspend fun addFamilyMember(
        @Path("id") primaryPatientId: Long,
        @Body request: CreatePatientRequest
    ): PatientResponse

    @GET("/api/medical-records/patient/{patientId}")
    suspend fun getMedicalRecords(
        @Path("patientId") patientId: Long
    ): List<MedicalRecordResponse>

    @POST("/api/medical-records")
    suspend fun createMedicalRecord(
        @Body request: CreateMedicalRecordRequest
    ): MedicalRecordResponse

    @GET("/api/analyses/patient/{patientId}")
    suspend fun getAnalyses(
        @Path("patientId") patientId: Long
    ): List<AnalysisResponse>

    @POST("/api/analyses")
    suspend fun createAnalysis(
        @Body request: CreateAnalysisRequest
    ): AnalysisResponse

    @Multipart
    @POST("/api/analyses/{id}/upload")
    suspend fun uploadAnalysisPdf(
        @Path("id") analysisId: Long,
        @Part file: MultipartBody.Part
    ): AnalysisResponse

    @GET("/api/appointments/doctor/{doctorId}/slots")
    suspend fun getDoctorSlots(
        @Path("doctorId") doctorId: Long,
        @Query("date") date: String
    ): List<AppointmentSlotResponse>

    @PUT("/api/appointments/{appointmentId}/reschedule")
    suspend fun rescheduleAppointment(
        @Path("appointmentId") appointmentId: Long,
        @Body request: RescheduleAppointmentRequest
    ): Map<String, String>

    @POST("/api/reminders")
    suspend fun createReminder(
        @Body request: CreateReminderRequest
    ): ReminderResponse

    @GET("/api/reminders/patient/{patientId}")
    suspend fun getReminders(
        @Path("patientId") patientId: Long
    ): List<ReminderResponse>

    @PUT("/api/reminders/{reminderId}/done")
    suspend fun markReminderDone(
        @Path("reminderId") reminderId: Long
    ): ReminderResponse

    @POST("/api/home-doctor")
    suspend fun createHomeDoctorRequest(
        @Body request: CreateHomeDoctorRequest
    ): HomeDoctorResponse

    @GET("/api/home-doctor/patient/{patientId}")
    suspend fun getHomeDoctorRequests(
        @Path("patientId") patientId: Long
    ): List<HomeDoctorResponse>

    @DELETE("/api/home-doctor/{requestId}")
    suspend fun cancelHomeDoctorRequest(
        @Path("requestId") requestId: Long
    ): Map<String, String>

    @GET("/api/assistant/chat/history")
    suspend fun getChatHistory(
        @Query("patientId") patientId: Long
    ): List<ChatMessageResponse>

    @POST("/api/assistant/chat/send")
    suspend fun sendChatMessage(
        @Body request: SendMessageRequest
    ): AssistantResponse
}
