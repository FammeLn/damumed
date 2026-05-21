package com.damumed.intelliheart.controller

import com.damumed.intelliheart.dto.CreateNotificationRequest
import com.damumed.intelliheart.dto.NotificationResponse
import com.damumed.intelliheart.service.NotificationService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/notifications")
class NotificationController(
    private val notificationService: NotificationService
) {
    @GetMapping
    fun getNotifications(
        @RequestParam(required = false) userId: Long?
    ): ResponseEntity<List<NotificationResponse>> {
        return ResponseEntity.ok(notificationService.getNotifications(userId))
    }

    @PostMapping
    fun createNotification(
        @RequestBody request: CreateNotificationRequest
    ): ResponseEntity<NotificationResponse> {
        return ResponseEntity.ok(notificationService.create(request))
    }
}
