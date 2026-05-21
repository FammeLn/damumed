package com.damumed.intelliheart.dto

data class CreateNotificationRequest(
    val title: String,
    val message: String,
    val userId: Long? = null
)

data class NotificationResponse(
    val id: Long,
    val title: String,
    val message: String,
    val createdAt: Long,
    val isRead: Boolean,
    val userId: Long? = null
)
