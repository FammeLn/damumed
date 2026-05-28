package com.damumed.intelliheart.service

import com.damumed.intelliheart.dto.CreateReminderRequestDto
import com.damumed.intelliheart.dto.ReminderResponseDto
import com.damumed.intelliheart.entity.Reminder
import com.damumed.intelliheart.repository.ReminderRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
@Transactional
class ReminderService(
    private val reminderRepository: ReminderRepository,
    private val notificationService: NotificationService
) {
    fun createReminder(request: CreateReminderRequestDto): ReminderResponseDto {
        val title = request.title.trim()
        if (title.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Тақырып міндетті")
        }

        val reminder = Reminder(
            patientId = request.patientId,
            title = title,
            note = request.note,
            scheduledAt = request.scheduledAt
        )
        val saved = reminderRepository.save(reminder)
        notificationService.createSystemNotification(
            title = "Еске салу құрылды",
            message = "Еске салу: ${request.title}",
            userId = request.patientId
        )
        return mapToDto(saved)
    }

    fun getReminders(patientId: Long): List<ReminderResponseDto> {
        return reminderRepository.findByPatientIdOrderByScheduledAtAsc(patientId).map { mapToDto(it) }
    }

    fun markDone(reminderId: Long): ReminderResponseDto {
        val reminder = reminderRepository.findById(reminderId)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Еске салу табылмады") }
        reminder.isDone = true
        val saved = reminderRepository.save(reminder)
        return mapToDto(saved)
    }

    private fun mapToDto(reminder: Reminder): ReminderResponseDto {
        return ReminderResponseDto(
            id = reminder.id ?: 0L,
            patientId = reminder.patientId,
            title = reminder.title,
            scheduledAt = reminder.scheduledAt,
            note = reminder.note,
            isDone = reminder.isDone
        )
    }
}
