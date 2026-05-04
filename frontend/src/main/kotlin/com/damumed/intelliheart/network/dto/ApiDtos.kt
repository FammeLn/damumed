package com.damumed.intelliheart.network.dto

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
