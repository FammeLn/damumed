package com.damumed.intelliheart.dto

data class TelegramAuthStartRequest(
    val phoneNumber: String
)

data class TelegramAuthStartResponse(
    val authRequestId: String,
    val botLink: String,
    val expiresAt: Long
)

data class TelegramAuthStatusResponse(
    val authRequestId: String,
    val status: String,
    val isAuthenticated: Boolean,
    val accessToken: String? = null,
    val patient: AuthPatientDto? = null,
    val expiresAt: Long
)

data class AuthPatientDto(
    val id: Long,
    val fullName: String,
    val phoneNumber: String
)
