package com.damumed.intelliheart.dto

import jakarta.validation.constraints.Email
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank

/**
 * DTO для создания нового профиля врача
 */
data class CreateDoctorRequestDto(
    @field:NotBlank
    val fullName: String,

    @field:NotBlank
    val specialization: String,

    @field:NotBlank
    val qualification: String,

    @field:Min(0)
    val experienceYears: Int,

    @field:NotBlank
    val licenseNumber: String,

    @field:NotBlank
    val phoneNumber: String,

    @field:Email
    @field:NotBlank
    val email: String,

    @field:NotBlank
    val workplace: String
)
