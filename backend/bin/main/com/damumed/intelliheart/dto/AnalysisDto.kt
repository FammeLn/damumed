package com.damumed.intelliheart.dto

import java.time.LocalDate

data class AnalysisResponseDto(
    val id: Long,
    val patientId: Long,
    val title: String,
    val date: LocalDate,
    val clinic: String,
    val status: String,
    val hasPdf: Boolean,
    val pdfUrl: String? = null
)

data class CreateAnalysisRequestDto(
    val patientId: Long,
    val title: String,
    val date: LocalDate,
    val clinic: String
)
