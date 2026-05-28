package com.damumed.intelliheart.dto

import java.time.LocalDateTime

data class RescheduleAppointmentRequestDto(
    val newDateTime: LocalDateTime
)

data class AppointmentSlotDto(
    val time: String
)
