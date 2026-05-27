package com.damumed.intelliheart.dto

import java.time.LocalDate

data class CreateMedicalRecordRequestDto(
    val patientId: Long,
    val doctorName: String,
    val specialization: String,
    val visitDate: LocalDate,
    val diagnosis: String? = null,
    val notes: String? = null
)

data class MedicalRecordResponseDto(
    val id: Long,
    val patientId: Long,
    val doctorName: String,
    val specialization: String,
    val visitDate: LocalDate,
    val diagnosis: String? = null,
    val notes: String? = null
)
