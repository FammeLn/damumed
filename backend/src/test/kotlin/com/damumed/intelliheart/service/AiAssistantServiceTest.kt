package com.damumed.intelliheart.ai

import com.damumed.intelliheart.dto.AssistantAction
import com.damumed.intelliheart.dto.AssistantRequest
import com.damumed.intelliheart.repository.ChatMessageRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNotNull
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.mock

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
        val restTemplate = mock<org.springframework.web.client.RestTemplate>()
        val chatMessageRepository = mock<ChatMessageRepository>()
        service = AiAssistantService(restTemplate, chatMessageRepository)
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
        assertEquals(AssistantAction.NAVIGATE_TO_APPOINTMENT, response.action)
        assertNotNull(response.text)
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
        assertEquals(AssistantAction.NAVIGATE_TO_RECORDS, response.action)
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
        assertEquals(AssistantAction.CALL_HOME_DOCTOR, response.action)
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
        assertEquals(AssistantAction.NONE, response.action)
        assertNotNull(response.text)
    }
}
