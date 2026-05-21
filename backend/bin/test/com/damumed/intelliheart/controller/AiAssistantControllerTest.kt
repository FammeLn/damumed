package com.damumed.intelliheart.ai

import com.damumed.intelliheart.dto.AssistantAction
import com.damumed.intelliheart.dto.AssistantRequest
import com.damumed.intelliheart.dto.AssistantResponse
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.verify
import org.springframework.http.HttpStatus

/**
 * Unit тесты для AiAssistantController
 * Проверяют корректность обработки HTTP запросов
 */
@ExtendWith(MockitoExtension::class)
@DisplayName("AiAssistantController - Тесты REST API")
class AiAssistantControllerTest {

    @Mock
    private lateinit var aiAssistantService: AiAssistantService

    @InjectMocks
    private lateinit var controller: AiAssistantController

    /**
     * Тест 1: Проверка успешной обработки запроса
     */
    @Test
    @DisplayName("Должен возвращать 200 OK при успешной обработке запроса")
    fun testProcessQuerySuccess() {
        // Дано
        val request = AssistantRequest(text = "жазылу дәрігерге")
        val expectedResponse = AssistantResponse(
            text = "Мен сізді дәрігерге жазуға көмектесемін",
            action = AssistantAction.NAVIGATE_TO_APPOINTMENT
        )

        given(aiAssistantService.processQuery(any())).willReturn(expectedResponse)

        // Когда
        val response = controller.processQuery(request)

        // Тогда
        assert(response.statusCode == HttpStatus.OK)
        assert(response.body?.action == AssistantAction.NAVIGATE_TO_APPOINTMENT)
        verify(aiAssistantService).processQuery(any())
    }

    /**
     * Тест 2: Проверка вызова сервиса
     */
    @Test
    @DisplayName("Должен вызвать AiAssistantService при получении запроса")
    fun testServiceInvocation() {
        // Дано
        val request = AssistantRequest(text = "талдау")
        val expectedResponse = AssistantResponse(
            text = "Ваша медицинская карта",
            action = AssistantAction.NAVIGATE_TO_RECORDS
        )

        given(aiAssistantService.processQuery(any())).willReturn(expectedResponse)

        // Когда
        controller.processQuery(request)

        // Тогда
        verify(aiAssistantService).processQuery(request)
    }
}
