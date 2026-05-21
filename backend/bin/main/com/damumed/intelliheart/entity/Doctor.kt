package com.damumed.intelliheart.entity

import jakarta.persistence.*

/**
 * Сущность Врач
 * Представляет профиль врача с квалификационной информацией
 */
@Entity
@Table(name = "doctors")
class Doctor(
    // Идентификатор врача
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    // Полное имя врача
    @Column(nullable = false)
    val fullName: String = "",

    // Специализация врача (например: "Кардиолог", "Невролог")
    @Column(nullable = false)
    val specialization: String = "",

    // Квалификационная категория (например: "Первая категория", "Высшая категория")
    @Column(nullable = false)
    val qualification: String = "",

    // Стаж работы в годах
    @Column(nullable = false)
    val experienceYears: Int = 0,

    // Средний рейтинг врача (0.0 - 5.0)
    @Column(nullable = false)
    var rating: Double = 0.0,

    // Номер лицензии врача
    @Column(unique = true, nullable = false)
    val licenseNumber: String = "",

    // Номер телефона кабинета или мобильный
    @Column(nullable = false)
    val phoneNumber: String = "",

    // Email для контакта
    @Column(unique = true, nullable = false)
    val email: String = "",

    // Место работы / название клиники
    @Column(nullable = false)
    val workplace: String = "",

    // Количество отзывов/рейтингов
    @Column(nullable = false)
    var ratingCount: Int = 0,

    // Дата регистрации в системе
    @Column(nullable = false, updatable = false)
    val createdAt: Long = System.currentTimeMillis(),

    // Дата последнего обновления
    @Column(nullable = false)
    var updatedAt: Long = System.currentTimeMillis(),

    // Флаг активности профиля врача
    @Column(nullable = false)
    var isActive: Boolean = true
)
