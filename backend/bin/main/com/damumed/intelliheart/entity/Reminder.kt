package com.damumed.intelliheart.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "reminders")
class Reminder(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val patientId: Long = 0L,

    @Column(nullable = false)
    val title: String = "",

    @Column(columnDefinition = "TEXT")
    val note: String? = null,

    @Column(nullable = false)
    val scheduledAt: LocalDateTime = LocalDateTime.now(),

    @Column(nullable = false)
    var isDone: Boolean = false,

    @Column(nullable = false, updatable = false)
    val createdAt: Long = System.currentTimeMillis()
)
