package com.damumed.intelliheart.dto

import java.time.LocalDateTime

/**
 * DTO для отправки информации о записи к врачу на клиент
 */
data class AppointmentResponseDto(
    // Идентификатор записи
    val id: Long,

    // Информация о пациенте
    val patient: AppointmentPatientDto,

    // Информация о враче
    val doctor: AppointmentDoctorDto,

    // Дата и время приема
    val appointmentDateTime: LocalDateTime,

    // Продолжительность приема в минутах
    val durationMinutes: Int,

    // Статус записи (на казахском языке)
    val status: String,

    // Причина или описание проблемы для приема
    val reasonForVisit: String? = null,

    // Примечания врача (заполняется после приема)
    val doctorNotes: String? = null,

    // Диагноз, если установлен
    val diagnosis: String? = null,

    // Рекомендации врача
    val recommendations: String? = null,

    // Место проведения приема (очное или онлайн)
    val appointmentType: String
)

/**
 * Вложенный DTO с информацией о пациенте в записи
 */
data class AppointmentPatientDto(
    val id: Long,
    val fullName: String,
    val phoneNumber: String
)

/**
 * Вложенный DTO с информацией о враче в записи
 */
data class AppointmentDoctorDto(
    val id: Long,
    val fullName: String,
    val specialization: String,
    val phoneNumber: String,
    val workplace: String
)
