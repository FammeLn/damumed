package com.damumed.intelliheart.entity

import jakarta.persistence.*
import java.time.LocalDateTime

@Entity
@Table(name = "chat_messages")
class ChatMessage(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(name = "patient_id", nullable = false)
    val patientId: Long = 0,

    @Column(name = "sender", nullable = false)
    val sender: String = "", // "USER" or "BOT"

    @Column(name = "text", nullable = false, length = 1000)
    val text: String = "",

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now()
)
