package com.damumed.intelliheart.controller

import com.damumed.intelliheart.dto.TelegramAuthStartRequest
import com.damumed.intelliheart.dto.TelegramAuthStartResponse
import com.damumed.intelliheart.dto.TelegramAuthStatusResponse
import com.damumed.intelliheart.service.TelegramAuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth/telegram")
class TelegramAuthController(
    private val telegramAuthService: TelegramAuthService
) {
    @PostMapping("/start")
    fun startAuth(
        @RequestBody request: TelegramAuthStartRequest
    ): ResponseEntity<TelegramAuthStartResponse> {
        return ResponseEntity.ok(telegramAuthService.startAuth(request))
    }

    @GetMapping("/status/{authRequestId}")
    fun getAuthStatus(
        @PathVariable authRequestId: String
    ): ResponseEntity<TelegramAuthStatusResponse> {
        return ResponseEntity.ok(telegramAuthService.getStatus(authRequestId))
    }
}
