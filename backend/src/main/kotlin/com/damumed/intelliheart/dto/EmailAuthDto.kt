package com.damumed.intelliheart.dto

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
