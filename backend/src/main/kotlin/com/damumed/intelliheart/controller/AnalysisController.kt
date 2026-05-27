package com.damumed.intelliheart.controller

import com.damumed.intelliheart.dto.AnalysisResponseDto
import com.damumed.intelliheart.dto.CreateAnalysisRequestDto
import com.damumed.intelliheart.service.AnalysisService
import com.damumed.intelliheart.service.FileStorageService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.core.io.Resource
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/analyses")
@Tag(name = "Analyses", description = "API для управления медицинскими анализами")
class AnalysisController(
    private val analysisService: AnalysisService,
    private val fileStorageService: FileStorageService
) {

    @Operation(summary = "Получить список анализов пациента")
    @GetMapping("/patient/{patientId}")
    fun getPatientAnalyses(@PathVariable patientId: Long): ResponseEntity<List<AnalysisResponseDto>> {
        return ResponseEntity.ok(analysisService.getAnalysesByPatientId(patientId))
    }

    @Operation(summary = "Создать карточку анализа")
    @PostMapping
    fun createAnalysis(@RequestBody request: CreateAnalysisRequestDto): ResponseEntity<AnalysisResponseDto> {
        return ResponseEntity.ok(analysisService.createAnalysis(request))
    }

    @Operation(summary = "Загрузить PDF документ для анализа")
    @PostMapping("/{id}/upload", consumes = [MediaType.MULTIPART_FORM_DATA_VALUE])
    fun uploadPdf(
        @PathVariable id: Long,
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<AnalysisResponseDto> {
        return ResponseEntity.ok(analysisService.uploadAnalysisPdf(id, file))
    }

    @Operation(summary = "Скачать PDF документ анализа")
    @GetMapping("/{id}/download")
    fun downloadPdf(@PathVariable id: Long): ResponseEntity<Resource> {
        val fileName = analysisService.getAnalysisPdfFileName(id)
        val resource = fileStorageService.loadFileAsResource(fileName)

        return ResponseEntity.ok()
            .contentType(MediaType.APPLICATION_PDF)
            .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"analysis_$id.pdf\"")
            .body(resource)
    }
}
