package com.damumed.intelliheart.ai

import com.damumed.intelliheart.dto.AssistantRequest
import com.damumed.intelliheart.dto.AssistantResponse
import com.damumed.intelliheart.dto.ChatMessageResponseDto
import com.damumed.intelliheart.dto.SendMessageRequestDto
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * REST контроллер для голосового помощника и чата поддержки
 */
@RestController
@RequestMapping("/api/assistant")
class AiAssistantController(
    private val aiAssistantService: AiAssistantService
) {

    /**
     * POST /api/assistant/query
     * Обработать запрос голосового помощника (интент-анализ)
     */
    @PostMapping("/query")
    fun processQuery(
        @RequestBody request: AssistantRequest
    ): ResponseEntity<AssistantResponse> {
        return ResponseEntity.ok(aiAssistantService.processQuery(request))
    }

    /**
     * GET /api/assistant/chat/history
     * Получить историю чата по ID пациента
     */
    @GetMapping("/chat/history")
    fun getChatHistory(
        @RequestParam patientId: Long
    ): ResponseEntity<List<ChatMessageResponseDto>> {
        return ResponseEntity.ok(aiAssistantService.getChatHistory(patientId))
    }

    /**
     * POST /api/assistant/chat/send
     * Отправить сообщение в чат поддержки и получить ответ бота
     */
    @PostMapping("/chat/send")
    fun sendChatMessage(
        @RequestBody request: SendMessageRequestDto
    ): ResponseEntity<AssistantResponse> {
        return ResponseEntity.ok(
            aiAssistantService.processAndSaveChatMessage(
                patientId = request.patientId,
                text = request.text
            )
        )
    }
}

