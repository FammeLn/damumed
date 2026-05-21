package com.damumed.intelliheart.entity

import jakarta.persistence.*
import java.time.LocalDateTime

/**
 * Сущность Запись к врачу
 * Представляет запись пациента на прием к врачу
 */
@Entity
@Table(name = "appointments")
class Appointment(
    // Идентификатор записи
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    // Связь с пациентом (ManyToOne)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "patient_id", nullable = false)
    val patient: Patient? = null,

    // Связь с врачом (ManyToOne)
    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "doctor_id", nullable = false)
    val doctor: Doctor? = null,

    // Дата и время приема
    @Column(nullable = false)
    var appointmentDateTime: LocalDateTime? = null,

    // Продолжительность приема в минутах
    @Column(nullable = false)
    val durationMinutes: Int = 30,

    // Статус записи (перечисление)
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: AppointmentStatus = AppointmentStatus.PENDING,

    // Причина или описание проблемы для приема
    @Column(columnDefinition = "TEXT")
    val reasonForVisit: String? = null,

    // Примечания врача (заполняется после приема)
    @Column(columnDefinition = "TEXT")
    var doctorNotes: String? = null,

    // Диагноз, если установлен
    @Column(columnDefinition = "TEXT")
    var diagnosis: String? = null,

    // Рекомендации врача
    @Column(columnDefinition = "TEXT")
    var recommendations: String? = null,

    // Место проведения приема (очное или онлайн)
    @Column(nullable = false)
    val appointmentType: String = "ONSITE",

    // Дата создания записи
    @Column(nullable = false, updatable = false)
    val createdAt: Long = System.currentTimeMillis(),

    // Дата последнего обновления
    @Column(nullable = false)
    var updatedAt: Long = System.currentTimeMillis(),

    // Напоминание отправлено пациенту (флаг)
    @Column(nullable = false)
    var reminderSent: Boolean = false
)
