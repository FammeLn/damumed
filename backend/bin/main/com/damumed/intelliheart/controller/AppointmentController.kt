package com.damumed.intelliheart.controller

import com.damumed.intelliheart.dto.AppointmentResponseDto
import com.damumed.intelliheart.dto.CreateAppointmentRequestDto
import com.damumed.intelliheart.service.AppointmentService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * REST контроллер для работы с записями к врачам
 * Предоставляет API эндпоинты для создания, получения и отмены записей
 */
@RestController
@RequestMapping("/api/appointments")
class AppointmentController(
    private val appointmentService: AppointmentService
) {

    /**
     * POST /api/appointments/book
     * Создать новую запись к врачу
     * @param requestDto данные для создания записи
     * @return информация о созданной записи
     */
    @PostMapping("/book")
    fun createAppointment(
        @RequestBody requestDto: CreateAppointmentRequestDto
    ): ResponseEntity<AppointmentResponseDto> {
        val appointment = appointmentService.createAppointment(requestDto)
        return ResponseEntity.status(HttpStatus.CREATED).body(appointment)
    }

    /**
     * GET /api/appointments/patient/{patientId}
     * Получить все записи пациента (историю прямо и прошлых приемов)
     * @param patientId идентификатор пациента
     * @return список всех записей пациента, отсортированный по времени приема (новые первыми)
     */
    @GetMapping("/patient/{patientId}")
    fun getPatientAppointments(
        @PathVariable patientId: Long
    ): ResponseEntity<List<AppointmentResponseDto>> {
        val appointments = appointmentService.getPatientAppointments(patientId)
        return ResponseEntity.ok(appointments)
    }

    /**
     * GET /api/appointments/patient/{patientId}/active
     * Получить активные (ожидающие) записи пациента
     * @param patientId идентификатор пациента
     * @return список активных записей (статусы: PENDING, RESCHEDULED)
     */
    @GetMapping("/patient/{patientId}/active")
    fun getPatientActiveAppointments(
        @PathVariable patientId: Long
    ): ResponseEntity<List<AppointmentResponseDto>> {
        val appointments = appointmentService.getPatientActiveAppointments(patientId)
        return ResponseEntity.ok(appointments)
    }

    /**
     * GET /api/appointments/{appointmentId}
     * Получить информацию о конкретной записи
     * @param appointmentId идентификатор записи
     * @return информация о записи
     */
    @GetMapping("/{appointmentId}")
    fun getAppointmentById(
        @PathVariable appointmentId: Long
    ): ResponseEntity<AppointmentResponseDto> {
        val appointment = appointmentService.getAppointmentById(appointmentId)
        return ResponseEntity.ok(appointment)
    }

    /**
     * DELETE /api/appointments/{appointmentId}
     * Отменить запись к врачу
     * @param appointmentId идентификатор записи
     * @return сообщение об успешной отмене
     */
    @DeleteMapping("/{appointmentId}")
    fun cancelAppointment(
        @PathVariable appointmentId: Long
    ): ResponseEntity<Map<String, String>> {
        appointmentService.cancelAppointment(appointmentId)
        return ResponseEntity.ok(mapOf("message" to "Өтінім сәтті болдырылды"))
    }
}
