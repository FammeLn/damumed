package com.damumed.intelliheart.ui.auth

data class AuthSession(
    val userId: Long,
    val email: String,
    val accessToken: String,
    val expiresAt: Long,
    val patientId: Long? = null
)

data class AuthState(
    val isAuthenticated: Boolean = false,
    val session: AuthSession? = null
)
