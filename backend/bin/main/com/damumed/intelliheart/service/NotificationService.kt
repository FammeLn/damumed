package com.damumed.intelliheart.service

import com.damumed.intelliheart.dto.CreateNotificationRequest
import com.damumed.intelliheart.dto.NotificationResponse
import com.damumed.intelliheart.entity.AppNotification
import com.damumed.intelliheart.repository.AppNotificationRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
@Transactional
class NotificationService(
    private val notificationRepository: AppNotificationRepository
) {
    fun create(request: CreateNotificationRequest): NotificationResponse {
        val title = request.title.trim()
        val message = request.message.trim()

        if (title.isBlank() || message.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Тақырып пен мәтін міндетті")
        }

        val notification = AppNotification(
            title = title,
            message = message,
            userId = request.userId
        )

        return mapToResponse(notificationRepository.save(notification))
    }

    fun getNotifications(userId: Long?): List<NotificationResponse> {
        val notifications = if (userId == null) {
            notificationRepository.findAllByUserIdIsNullOrderByCreatedAtDesc()
        } else {
            notificationRepository.findAllForUser(userId)
        }

        return notifications.map { mapToResponse(it) }
    }

    private fun mapToResponse(notification: AppNotification): NotificationResponse {
        return NotificationResponse(
            id = notification.id ?: 0L,
            title = notification.title,
            message = notification.message,
            createdAt = notification.createdAt,
            isRead = notification.isRead,
            userId = notification.userId
        )
    }

    fun createSystemNotification(title: String, message: String, userId: Long?) {
        if (title.isBlank() || message.isBlank()) {
            return
        }

        notificationRepository.save(
            AppNotification(
                title = title,
                message = message,
                userId = userId
            )
        )
    }
}
