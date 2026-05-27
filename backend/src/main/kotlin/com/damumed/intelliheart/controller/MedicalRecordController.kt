package com.damumed.intelliheart.controller

import com.damumed.intelliheart.dto.CreateMedicalRecordRequestDto
import com.damumed.intelliheart.dto.MedicalRecordResponseDto
import com.damumed.intelliheart.service.MedicalRecordService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/medical-records")
class MedicalRecordController(
    private val medicalRecordService: MedicalRecordService
) {
    @PostMapping
    fun createRecord(
        @RequestBody request: CreateMedicalRecordRequestDto
    ): ResponseEntity<MedicalRecordResponseDto> {
        return ResponseEntity.ok(medicalRecordService.createRecord(request))
    }

    @GetMapping("/patient/{patientId}")
    fun getPatientRecords(
        @PathVariable patientId: Long
    ): ResponseEntity<List<MedicalRecordResponseDto>> {
        return ResponseEntity.ok(medicalRecordService.getRecordsForPatient(patientId))
    }
}
