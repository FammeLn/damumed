package com.damumed.intelliheart.dto

import java.time.LocalDateTime

data class ChatMessageResponseDto(
    val id: Long,
    val patientId: Long,
    val sender: String,
    val text: String,
    val createdAt: LocalDateTime
)

data class SendMessageRequestDto(
    val patientId: Long,
    val text: String
)
