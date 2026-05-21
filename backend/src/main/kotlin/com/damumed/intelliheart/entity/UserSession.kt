package com.damumed.intelliheart.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "user_sessions")
class UserSession(
    @Id
    val token: String = "",

    @Column(nullable = false)
    val userId: Long = 0L,

    @Column(nullable = false, updatable = false)
    val createdAt: Long = System.currentTimeMillis(),

    @Column(nullable = false)
    val expiresAt: Long = System.currentTimeMillis()
)
