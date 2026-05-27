package com.damumed.intelliheart.dto

data class CreateHomeDoctorRequestDto(
    val patientId: Long,
    val symptoms: List<String>,
    val address: String
)

data class HomeDoctorResponseDto(
    val id: Long,
    val patientId: Long,
    val symptoms: List<String>,
    val address: String,
    val status: String,
    val createdAt: Long
)
