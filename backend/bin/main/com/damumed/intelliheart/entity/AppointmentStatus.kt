package com.damumed.intelliheart.entity

/**
 * Статусы записи к врачу
 * Все значения на казахском языке для отправки клиенту
 */
enum class AppointmentStatus(val displayName: String) {
    // Ожидается прием
    PENDING("Күтілуде"),
    // Завершено
    COMPLETED("Аяқталды"),
    // Отменено
    CANCELLED("Болдырылмады"),
    // Перенесено
    RESCHEDULED("Ауыстырылды")
}
