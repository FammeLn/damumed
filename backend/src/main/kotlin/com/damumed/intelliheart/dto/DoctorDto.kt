package com.damumed.intelliheart.dto

/**
 * DTO для отправки информации о враче на клиент
 */
data class DoctorDto(
    // Идентификатор врача
    val id: Long,

    // Полное имя врача
    val fullName: String,

    // Специализация врача
    val specialization: String,

    // Квалификационная категория
    val qualification: String,

    // Стаж работы в годах
    val experienceYears: Int,

    // Средний рейтинг врача (0.0 - 5.0)
    val rating: Double,

    // Количество отзывов
    val ratingCount: Int,

    // Место работы / название клиники
    val workplace: String,

    // Номер телефона
    val phoneNumber: String,

    // Email для контакта
    val email: String
)
