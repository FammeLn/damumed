package com.damumed.intelliheart.ai

import com.damumed.intelliheart.dto.AssistantAction
import com.damumed.intelliheart.dto.AssistantRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test

/**
 * Unit тесты для AiAssistantService
 * Проверяют корректность логики обработки естественного языка
 */
@DisplayName("AiAssistantService - Тесты логики NLP")
class AiAssistantServiceTest {

    private lateinit var service: AiAssistantService

    @BeforeEach
    fun setUp() {
        // Инициализируем сервис перед каждым тестом
        service = AiAssistantService()
    }

    /**
     * Тест 1: Проверка распознавания интента "Запись к врачу"
     */
    @Test
    @DisplayName("Должен распознать интент 'Запись к врачу' с ключевым словом 'жазылу'")
    fun testAppointmentIntentWithKazakhKeyword() {
        // Дано
        val request = AssistantRequest(text = "Мне нужно жазылу к врачу")

        // Когда
        val response = service.processQuery(request)

        // Тогда
        assertEquals(AssistantAction.NAVIGATE_TO_APPOINTMENT.name, response.action)
        assertEquals("Мен сізді дәрігерге жазуға көмектесемін. Қай дәрігерге жазылғыңыз келеді?", response.text)
    }

    /**
     * Тест 2: Проверка распознавания интента "Медицинская карта"
     */
    @Test
    @DisplayName("Должен распознать интент 'Медицинская карта' с ключевым словом 'талдау'")
    fun testMedicalRecordIntent() {
        // Дано
        val request = AssistantRequest(text = "Покажи мне мои талдау")

        // Когда
        val response = service.processQuery(request)

        // Тогда
        assertEquals(AssistantAction.NAVIGATE_TO_RECORDS.name, response.action)
        assertNotNull(response.text)
    }

    /**
     * Тест 3: Проверка распознавания интента "Вызов врача на дом"
     */
    @Test
    @DisplayName("Должен распознать интент 'Вызов врача' с ключевым словом 'үйге'")
    fun testCallHomeDoctorIntent() {
        // Дано
        val request = AssistantRequest(text = "Пожалуйста, шақыру дәрігер үйге")

        // Когда
        val response = service.processQuery(request)

        // Тогда
        assertEquals(AssistantAction.CALL_HOME_DOCTOR.name, response.action)
    }

    /**
     * Тест 4: Проверка обработки неизвестного запроса
     */
    @Test
    @DisplayName("Должен вернуть действие NONE для неизвестного запроса")
    fun testUnknownQuery() {
        // Дано
        val request = AssistantRequest(text = "Сколько будет два плюс два?")

        // Когда
        val response = service.processQuery(request)

        // Тогда
        assertEquals(AssistantAction.NONE.name, response.action)
        assertEquals(
            "Кешіріңіз, мен сізді түсінбедім. Сұрағыңызды қайталаңызшы. Мысалы: 'дәрігерге жазылу' немесе 'медициналық картамды қарау'.",
            response.text
        )
    }

    /**
     * Тест 5: Проверка анализа текста (расширенный анализ)
     */
    @Test
    @DisplayName("Должен корректно анализировать найденные ключевые слова")
    fun testTextAnalysis() {
        // Дано
        val text = "Мне нужно жазылу к врачу и посмотреть свои талдау"

        // Когда
        val analysis = service.analyzeText(text)

        // Тогда
        assertNotNull(analysis)
        assertNotNull(analysis["foundIntents"])
    }

    /**
     * Тест 6: Проверка регистронезависимости
     */
    @Test
    @DisplayName("Должен работать независимо от регистра букв")
    fun testCaseInsensitivity() {
        // Дано
        val request = AssistantRequest(text = "ЖАЗЫЛУ К ВРАЧУ ПРОШУ")

        // Когда
        val response = service.processQuery(request)

        // Тогда
        assertEquals(AssistantAction.NAVIGATE_TO_APPOINTMENT.name, response.action)
    }

    /**
     * Тест 7: Проверка профиля пользователя
     */
    @Test
    @DisplayName("Должен распознать интент 'Профиль' с ключевым словом 'жеке кабинет'")
    fun testProfileIntent() {
        // Дано
        val request = AssistantRequest(text = "Покажи мою жеке кабинет")

        // Когда
        val response = service.processQuery(request)

        // Тогда
        assertEquals(AssistantAction.NAVIGATE_TO_PROFILE.name, response.action)
    }
}
