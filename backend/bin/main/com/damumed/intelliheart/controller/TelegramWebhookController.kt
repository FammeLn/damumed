package com.damumed.intelliheart.controller

import com.damumed.intelliheart.service.TelegramAuthService
import com.fasterxml.jackson.databind.JsonNode
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/telegram")
class TelegramWebhookController(
    private val telegramAuthService: TelegramAuthService
) {
    @PostMapping("/webhook")
    fun handleWebhook(
        @RequestBody update: JsonNode
    ): ResponseEntity<Map<String, String>> {
        telegramAuthService.handleWebhook(update)
        return ResponseEntity.ok(mapOf("status" to "ok"))
    }
}
