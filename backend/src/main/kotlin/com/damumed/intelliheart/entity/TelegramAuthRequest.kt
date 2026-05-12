package com.damumed.intelliheart.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.util.UUID

@Entity
@Table(name = "telegram_auth_requests")
class TelegramAuthRequest(
    @Id
    val id: String = UUID.randomUUID().toString(),

    @Column(nullable = false)
    val phoneNumber: String = "",

    @Column(nullable = false)
    val normalizedPhoneNumber: String = "",

    @Column(nullable = false)
    val patientId: Long = 0L,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: TelegramAuthStatus = TelegramAuthStatus.PENDING,

    @Column
    var telegramChatId: Long? = null,

    @Column
    var telegramUserId: Long? = null,

    @Column
    var accessToken: String? = null,

    @Column(nullable = false, updatable = false)
    val createdAt: Long = System.currentTimeMillis(),

    @Column(nullable = false)
    var expiresAt: Long = System.currentTimeMillis(),

    @Column
    var verifiedAt: Long? = null
)

enum class TelegramAuthStatus {
    PENDING,
    VERIFIED,
    FAILED,
    EXPIRED
}
