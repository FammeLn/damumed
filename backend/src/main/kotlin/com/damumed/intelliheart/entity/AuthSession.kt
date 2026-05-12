package com.damumed.intelliheart.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "auth_sessions")
class AuthSession(
    @Id
    val token: String = "",

    @Column(nullable = false)
    val patientId: Long = 0L,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    val provider: AuthProvider = AuthProvider.TELEGRAM,

    @Column(nullable = false, updatable = false)
    val createdAt: Long = System.currentTimeMillis(),

    @Column(nullable = false)
    var expiresAt: Long = System.currentTimeMillis()
)

enum class AuthProvider {
    TELEGRAM
}
