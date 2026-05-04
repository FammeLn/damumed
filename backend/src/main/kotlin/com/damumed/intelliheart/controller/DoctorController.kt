package com.damumed.intelliheart.controller

import com.damumed.intelliheart.dto.DoctorDto
import com.damumed.intelliheart.service.DoctorService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/**
 * REST контроллер для работы с врачами
 * Предоставляет API эндпоинты для получения информации о врачах
 */
@RestController
@RequestMapping("/api/doctors")
class DoctorController(
    private val doctorService: DoctorService
) {

    /**
     * GET /api/doctors
     * Получить список всех активных врачей
     * @return список врачей
     */
    @GetMapping
    fun getAllDoctors(): ResponseEntity<List<DoctorDto>> {
        val doctors = doctorService.getAllActiveDoctors()
        return ResponseEntity.ok(doctors)
    }

    /**
     * GET /api/doctors/sorted
     * Получить список врачей отсортированный по рейтингу (высший рейтинг первым)
     * @return список врачей, отсортированный по рейтингу
     */
    @GetMapping("/sorted")
    fun getDoctorsSortedByRating(): ResponseEntity<List<DoctorDto>> {
        val doctors = doctorService.getDoctorsSortedByRating()
        return ResponseEntity.ok(doctors)
    }

    /**
     * GET /api/doctors/specialization/{specialization}
     * Получить список врачей по специализации
     * @param specialization специализация врача (например: "Кардиолог", "Невролог")
     * @return список врачей данной специализации, отсортированный по рейтингу
     */
    @GetMapping("/specialization/{specialization}")
    fun getDoctorsBySpecialization(
        @PathVariable specialization: String
    ): ResponseEntity<List<DoctorDto>> {
        val doctors = doctorService.getDoctorsBySpecialization(specialization)
        return ResponseEntity.ok(doctors)
    }

    /**
     * GET /api/doctors/{id}
     * Получить информацию о враче по идентификатору
     * @param id идентификатор врача
     * @return информация о враче
     */
    @GetMapping("/{id}")
    fun getDoctorById(
        @PathVariable id: Long
    ): ResponseEntity<DoctorDto> {
        val doctor = doctorService.getDoctorById(id)
        return ResponseEntity.ok(doctor)
    }

    /**
     * GET /api/doctors/search
     * Поиск врачей по имени
     * @param query поисковый запрос (часть имени врача)
     * @return список врачей, соответствующих поисковому запросу
     */
    @GetMapping("/search")
    fun searchDoctors(
        @RequestParam query: String
    ): ResponseEntity<List<DoctorDto>> {
        val doctors = doctorService.searchDoctorsByName(query)
        return ResponseEntity.ok(doctors)
    }
}
