package com.damumed.intelliheart.entity

import jakarta.persistence.*
import java.time.LocalDate

/**
 * Сущность Пациент
 * Представляет профиль пациента с персональной информацией
 */
@Entity
@Table(name = "patients")
class Patient(
    // Идентификатор пациента
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    // Индивидуальный идентификационный номер (ИИН)
    @Column(unique = true, nullable = false)
    val iin: String = "",

    // Полное имя пациента
    @Column(nullable = false)
    val fullName: String = "",

    // Дата рождения
    @Column(nullable = false)
    val dateOfBirth: LocalDate? = null,

    // Пол пациента (М/Ж)
    @Column(nullable = false)
    val gender: String = "",

    // Номер телефона (используется для авторизации)
    @Column(unique = true, nullable = false)
    val phoneNumber: String = "",

    // Дополнительная информация (аллергии, хронические заболевания и т.д.)
    @Column(columnDefinition = "TEXT")
    val medicalHistory: String? = null,

    // Адрес проживания
    @Column(nullable = false)
    val address: String = "",

    // Дата регистрации в системе
    @Column(nullable = false, updatable = false)
    val createdAt: Long = System.currentTimeMillis(),

    // Дата последнего обновления
    @Column(nullable = false)
    var updatedAt: Long = System.currentTimeMillis(),

    // Флаг активности аккаунта
    @Column(nullable = false)
    var isActive: Boolean = true
)
