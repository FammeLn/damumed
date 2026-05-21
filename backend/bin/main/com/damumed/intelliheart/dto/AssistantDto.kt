package com.damumed.intelliheart.dto

/**
 * DTO для запроса к голосовому помощнику
 * Содержит распознанный текст от пользователя
 */
data class AssistantRequest(
    // Распознанный текст от пользователя
    val text: String
)

/**
 * Перечисление для типов действий, которые должен выполнить голосовой помощник
 */
enum class AssistantAction {
    // Без действия - просто ответить
    NONE,

    // Переход на экран записи к врачу
    NAVIGATE_TO_APPOINTMENT,

    // Переход на экран медицинской карты
    NAVIGATE_TO_RECORDS,

    // Вызвать врача на дом
    CALL_HOME_DOCTOR,

    // Показать профиль пользователя
    NAVIGATE_TO_PROFILE
}

/**
 * DTO для ответа голосового помощника
 * Содержит текстовый ответ и рекомендуемое действие
 */
data class AssistantResponse(
    // Текстовый ответ на казахском языке
    val text: String,

    // Действие, которое должно быть выполнено на клиенте
    val action: AssistantAction = AssistantAction.NONE,

    // Дополнительные данные (например, ID врача для навигации)
    val metadata: Map<String, String> = emptyMap()
)
