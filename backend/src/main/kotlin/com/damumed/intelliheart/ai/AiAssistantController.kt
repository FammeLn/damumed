package com.damumed.intelliheart.ai

import com.damumed.intelliheart.dto.AssistantRequest
import com.damumed.intelliheart.dto.AssistantResponse
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * REST контроллер для голосового помощника
 * Обрабатывает запросы от мобильного приложения и возвращает умные ответы
 */
@RestController
@RequestMapping("/api/assistant")
class AiAssistantController(
    private val aiAssistantService: AiAssistantService
) {

    /**
     * POST /api/assistant/query
     * Обработать запрос голосового помощника
     * @param request запрос с распознанным текстом от пользователя
     * @return ответ помощника с текстом и рекомендуемым действием
     */
    @PostMapping("/query")
    fun processQuery(
        @RequestBody request: AssistantRequest
    ): ResponseEntity<AssistantResponse> {
        // Обрабатываем запрос через сервис AI
        val response = aiAssistantService.processQuery(request)

        // Возвращаем ответ с HTTP 200
        return ResponseEntity.ok(response)
    }
}
