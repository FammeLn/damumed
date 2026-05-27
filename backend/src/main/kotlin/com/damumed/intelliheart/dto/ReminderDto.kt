package com.damumed.intelliheart.dto

import java.time.LocalDateTime

data class CreateReminderRequestDto(
    val patientId: Long,
    val title: String,
    val scheduledAt: LocalDateTime,
    val note: String? = null
)

data class ReminderResponseDto(
    val id: Long,
    val patientId: Long,
    val title: String,
    val scheduledAt: LocalDateTime,
    val note: String? = null,
    val isDone: Boolean
)
