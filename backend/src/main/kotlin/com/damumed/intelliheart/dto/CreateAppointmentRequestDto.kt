package com.damumed.intelliheart.dto

import java.time.LocalDateTime

/**
 * DTO для запроса создания новой записи к врачу
 */
data class CreateAppointmentRequestDto(
    // Идентификатор пациента
    val patientId: Long,

    // Идентификатор врача
    val doctorId: Long,

    // Дата и время приема
    val appointmentDateTime: LocalDateTime,

    // Причина или описание проблемы для приема
    val reasonForVisit: String? = null,

    // Тип приема (ONSITE для очного, ONLINE для онлайн)
    val appointmentType: String = "ONSITE",

    // Продолжительность приема в минутах
    val durationMinutes: Int = 30
)
