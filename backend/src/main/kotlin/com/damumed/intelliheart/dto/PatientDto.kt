package com.damumed.intelliheart.dto

import java.time.LocalDate

data class CreatePatientRequestDto(
    val iin: String,
    val fullName: String,
    val dateOfBirth: LocalDate,
    val gender: String,
    val phoneNumber: String,
    val address: String,
    val medicalHistory: String? = null,
    val primaryPatientId: Long? = null,
    val relationType: String? = null
)

data class PatientDto(
    val id: Long,
    val iin: String,
    val fullName: String,
    val dateOfBirth: LocalDate,
    val gender: String,
    val phoneNumber: String,
    val address: String,
    val medicalHistory: String? = null,
    val isActive: Boolean,
    val primaryPatientId: Long? = null,
    val relationType: String? = null
)
