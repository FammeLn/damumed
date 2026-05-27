package com.damumed.intelliheart.controller

import com.damumed.intelliheart.dto.CreateHomeDoctorRequestDto
import com.damumed.intelliheart.dto.HomeDoctorResponseDto
import com.damumed.intelliheart.service.HomeDoctorService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/home-doctor")
class HomeDoctorController(
    private val homeDoctorService: HomeDoctorService
) {
    @PostMapping
    fun createRequest(
        @RequestBody request: CreateHomeDoctorRequestDto
    ): ResponseEntity<HomeDoctorResponseDto> {
        return ResponseEntity.ok(homeDoctorService.createRequest(request))
    }

    @GetMapping("/patient/{patientId}")
    fun getRequests(
        @PathVariable patientId: Long
    ): ResponseEntity<List<HomeDoctorResponseDto>> {
        return ResponseEntity.ok(homeDoctorService.getRequestsForPatient(patientId))
    }

    @DeleteMapping("/{requestId}")
    fun cancelRequest(
        @PathVariable requestId: Long
    ): ResponseEntity<Map<String, String>> {
        homeDoctorService.cancelRequest(requestId)
        return ResponseEntity.ok(mapOf("message" to "Өтінім болдырылды"))
    }
}
