package com.damumed.intelliheart.controller

import com.damumed.intelliheart.dto.CreatePatientRequestDto
import com.damumed.intelliheart.dto.PatientDto
import com.damumed.intelliheart.service.PatientService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/patients")
class PatientController(
    private val patientService: PatientService
) {
    @PostMapping
    fun createPatient(
        @RequestBody request: CreatePatientRequestDto
    ): ResponseEntity<PatientDto> {
        return ResponseEntity.ok(patientService.createPatient(request))
    }

    @GetMapping("/{id}")
    fun getPatient(
        @PathVariable id: Long
    ): ResponseEntity<PatientDto> {
        return ResponseEntity.ok(patientService.getPatientById(id))
    }

    @GetMapping("/{id}/family")
    fun getFamilyMembers(
        @PathVariable id: Long
    ): ResponseEntity<List<PatientDto>> {
        return ResponseEntity.ok(patientService.getFamilyMembers(id))
    }

    @PostMapping("/{id}/family")
    fun addFamilyMember(
        @PathVariable id: Long,
        @RequestBody request: CreatePatientRequestDto
    ): ResponseEntity<PatientDto> {
        return ResponseEntity.ok(patientService.createFamilyMember(id, request))
    }
}
