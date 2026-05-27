package com.damumed.intelliheart.service

import com.damumed.intelliheart.dto.AnalysisResponseDto
import com.damumed.intelliheart.dto.CreateAnalysisRequestDto
import com.damumed.intelliheart.entity.Analysis
import com.damumed.intelliheart.repository.AnalysisRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.multipart.MultipartFile

@Service
class AnalysisService(
    private val analysisRepository: AnalysisRepository,
    private val fileStorageService: FileStorageService
) {

    fun getAnalysesByPatientId(patientId: Long): List<AnalysisResponseDto> {
        val analyses = analysisRepository.findByPatientId(patientId)
        return analyses.map { mapToResponse(it) }
    }

    @Transactional
    fun createAnalysis(request: CreateAnalysisRequestDto): AnalysisResponseDto {
        val analysis = Analysis(
            patientId = request.patientId,
            title = request.title,
            date = request.date,
            clinic = request.clinic,
            status = "В ОБРАБОТКЕ"
        )
        val saved = analysisRepository.save(analysis)
        return mapToResponse(saved)
    }

    @Transactional
    fun uploadAnalysisPdf(id: Long, file: MultipartFile): AnalysisResponseDto {
        val analysis = analysisRepository.findById(id).orElseThrow { RuntimeException("Analysis not found with id $id") }
        
        val fileName = fileStorageService.storeFile(file)
        analysis.pdfFilePath = fileName
        analysis.status = "ГОТОВ"
        
        val updated = analysisRepository.save(analysis)
        return mapToResponse(updated)
    }

    private fun mapToResponse(analysis: Analysis): AnalysisResponseDto {
        return AnalysisResponseDto(
            id = analysis.id ?: 0,
            patientId = analysis.patientId,
            title = analysis.title,
            date = analysis.date ?: java.time.LocalDate.now(),
            clinic = analysis.clinic,
            status = analysis.status,
            hasPdf = !analysis.pdfFilePath.isNullOrEmpty(),
            pdfUrl = if (!analysis.pdfFilePath.isNullOrEmpty()) "/api/analyses/${analysis.id}/download" else null
        )
    }
    
    fun getAnalysisPdfFileName(id: Long): String {
        val analysis = analysisRepository.findById(id).orElseThrow { RuntimeException("Analysis not found with id $id") }
        return analysis.pdfFilePath ?: throw RuntimeException("PDF file not found for analysis $id")
    }
}
