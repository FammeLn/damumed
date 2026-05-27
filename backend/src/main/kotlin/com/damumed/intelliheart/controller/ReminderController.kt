package com.damumed.intelliheart.controller

import com.damumed.intelliheart.dto.CreateReminderRequestDto
import com.damumed.intelliheart.dto.ReminderResponseDto
import com.damumed.intelliheart.service.ReminderService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.bind.annotation.PutMapping

@RestController
@RequestMapping("/api/reminders")
class ReminderController(
    private val reminderService: ReminderService
) {
    @PostMapping
    fun createReminder(
        @RequestBody request: CreateReminderRequestDto
    ): ResponseEntity<ReminderResponseDto> {
        return ResponseEntity.ok(reminderService.createReminder(request))
    }

    @GetMapping("/patient/{patientId}")
    fun getReminders(
        @PathVariable patientId: Long
    ): ResponseEntity<List<ReminderResponseDto>> {
        return ResponseEntity.ok(reminderService.getReminders(patientId))
    }

    @PutMapping("/{reminderId}/done")
    fun markDone(
        @PathVariable reminderId: Long
    ): ResponseEntity<ReminderResponseDto> {
        return ResponseEntity.ok(reminderService.markDone(reminderId))
    }
}
