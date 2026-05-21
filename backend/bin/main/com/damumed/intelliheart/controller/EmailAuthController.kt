package com.damumed.intelliheart.controller

import com.damumed.intelliheart.dto.EmailAuthRequest
import com.damumed.intelliheart.dto.EmailAuthResponse
import com.damumed.intelliheart.service.EmailAuthService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
class EmailAuthController(
    private val emailAuthService: EmailAuthService
) {
    @PostMapping("/register")
    fun register(
        @RequestBody request: EmailAuthRequest
    ): ResponseEntity<EmailAuthResponse> {
        return ResponseEntity.ok(emailAuthService.register(request))
    }

    @PostMapping("/login")
    fun login(
        @RequestBody request: EmailAuthRequest
    ): ResponseEntity<EmailAuthResponse> {
        return ResponseEntity.ok(emailAuthService.login(request))
    }
}
