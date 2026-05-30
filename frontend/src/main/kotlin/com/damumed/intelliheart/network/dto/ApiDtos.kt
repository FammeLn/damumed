package com.damumed.intelliheart.network.dto

import java.time.LocalDate
import java.time.LocalDateTime

/**
 * DTO для информации о враче (полученное с бэкенда)
 */
data class DoctorResponse(
    val id: Long,
    val fullName: String,
    val specialization: String,
    val qualification: String,
    val experienceYears: Int,
    val rating: Double,
    val ratingCount: Int,
    val workplace: String,
    val phoneNumber: String,
    val email: String
)

/**
 * DTO для запроса создания записи к врачу
 */
data class CreateAppointmentRequest(
    val patientId: Long,
    val doctorId: Long,
    val appointmentDateTime: LocalDateTime,
    val reasonForVisit: String? = null,
    val appointmentType: String = "ONSITE",
    val durationMinutes: Int = 30
)

/**
 * DTO для информации о записи (полученное с бэкенда)
 */
data class AppointmentResponse(
    val id: Long,
    val patient: AppointmentPatientDto,
    val doctor: AppointmentDoctorDto,
    val appointmentDateTime: LocalDateTime,
    val durationMinutes: Int,
    val status: String,
    val reasonForVisit: String? = null,
    val doctorNotes: String? = null,
    val diagnosis: String? = null,
    val recommendations: String? = null,
    val appointmentType: String
)

/**
 * Вложенный DTO пациента в записи
 */
data class AppointmentPatientDto(
    val id: Long,
    val fullName: String,
    val phoneNumber: String
)

/**
 * Вложенный DTO врача в записи
 */
data class AppointmentDoctorDto(
    val id: Long,
    val fullName: String,
    val specialization: String,
    val phoneNumber: String,
    val workplace: String
)

/**
 * DTO для запроса к голосовому помощнику
 */
data class AssistantRequest(
    // Распознанный текст от пользователя
    val text: String
)

/**
 * DTO для ответа голосового помощника
 */
data class AssistantResponse(
    // Текстовый ответ на казахском языке
    val text: String,

    // Действие, которое должно быть выполнено на клиенте
    val action: String = "NONE",

    // Дополнительные данные (например, ID врача для навигации)
    val metadata: Map<String, String> = emptyMap()
)

data class TelegramAuthStartRequest(
    val phoneNumber: String
)

data class TelegramAuthStartResponse(
    val authRequestId: String,
    val botLink: String,
    val expiresAt: Long
)

data class TelegramAuthPatient(
    val id: Long,
    val fullName: String,
    val phoneNumber: String
)

data class TelegramAuthStatusResponse(
    val authRequestId: String,
    val status: String,
    val isAuthenticated: Boolean,
    val accessToken: String? = null,
    val patient: TelegramAuthPatient? = null,
    val expiresAt: Long
)

data class EmailAuthRequest(
    val email: String,
    val password: String
)

data class EmailAuthResponse(
    val userId: Long,
    val email: String,
    val accessToken: String,
    val expiresAt: Long,
    val patientId: Long? = null
)

data class CreateNotificationRequest(
    val title: String,
    val message: String,
    val userId: Long? = null
)

data class NotificationResponse(
    val id: Long,
    val title: String,
    val message: String,
    val createdAt: Long,
    val isRead: Boolean,
    val userId: Long? = null
)

data class CreatePatientRequest(
    val iin: String,
    val fullName: String,
    val dateOfBirth: LocalDate,
    val gender: String,
    val phoneNumber: String,
    val address: String,
    val medicalHistory: String? = null,
    val primaryPatientId: Long? = null,
    val relationType: String? = null,
    val userId: Long? = null
)

data class PatientResponse(
    val id: Long,
    val iin: String,
    val fullName: String,
    val dateOfBirth: LocalDate,
    val gender: String,
    val phoneNumber: String,
    val address: String,
    val medicalHistory: String? = null,
    val isActive: Boolean,
    val primaryPatientId: Long? = null,
    val relationType: String? = null,
    val userId: Long? = null
)

data class CreateMedicalRecordRequest(
    val patientId: Long,
    val doctorName: String,
    val specialization: String,
    val visitDate: LocalDate,
    val diagnosis: String? = null,
    val notes: String? = null
)

data class MedicalRecordResponse(
    val id: Long,
    val patientId: Long,
    val doctorName: String,
    val specialization: String,
    val visitDate: LocalDate,
    val diagnosis: String? = null,
    val notes: String? = null
)

data class CreateAnalysisRequest(
    val patientId: Long,
    val title: String,
    val date: LocalDate,
    val clinic: String
)

data class AnalysisResponse(
    val id: Long,
    val patientId: Long,
    val title: String,
    val date: LocalDate,
    val clinic: String,
    val status: String,
    val hasPdf: Boolean,
    val pdfUrl: String? = null
)

data class CreateReminderRequest(
    val patientId: Long,
    val title: String,
    val scheduledAt: LocalDateTime,
    val note: String? = null
)

data class ReminderResponse(
    val id: Long,
    val patientId: Long,
    val title: String,
    val scheduledAt: LocalDateTime,
    val note: String? = null,
    val isDone: Boolean
)

data class CreateHomeDoctorRequest(
    val patientId: Long,
    val symptoms: List<String>,
    val address: String
)

data class HomeDoctorResponse(
    val id: Long,
    val patientId: Long,
    val symptoms: List<String>,
    val address: String,
    val status: String,
    val createdAt: Long
)

data class AppointmentSlotResponse(
    val time: String
)

data class RescheduleAppointmentRequest(
    val newDateTime: LocalDateTime
)

data class ChatMessageResponse(
    val id: Long,
    val patientId: Long,
    val sender: String,
    val text: String,
    val createdAt: LocalDateTime
)

data class SendMessageRequest(
    val patientId: Long,
    val text: String
)

