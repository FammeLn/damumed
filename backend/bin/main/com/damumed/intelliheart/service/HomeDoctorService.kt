package com.damumed.intelliheart.service

import com.damumed.intelliheart.dto.CreateHomeDoctorRequestDto
import com.damumed.intelliheart.dto.HomeDoctorResponseDto
import com.damumed.intelliheart.entity.HomeDoctorRequest
import com.damumed.intelliheart.entity.HomeDoctorStatus
import com.damumed.intelliheart.exception.PatientNotFoundException
import com.damumed.intelliheart.repository.HomeDoctorRequestRepository
import com.damumed.intelliheart.repository.PatientRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class HomeDoctorService(
    private val homeDoctorRequestRepository: HomeDoctorRequestRepository,
    private val patientRepository: PatientRepository,
    private val notificationService: NotificationService
) {
    fun createRequest(request: CreateHomeDoctorRequestDto): HomeDoctorResponseDto {
        val patient = patientRepository.findById(request.patientId)
            .orElseThrow { PatientNotFoundException("Пациент табылмады (ID: ${request.patientId})") }

        val symptoms = request.symptoms.joinToString(",")
        val entity = HomeDoctorRequest(
            patient = patient,
            symptoms = symptoms,
            address = request.address,
            status = HomeDoctorStatus.PENDING
        )

        val saved = homeDoctorRequestRepository.save(entity)
        notificationService.createSystemNotification(
            title = "Үйге дәрігер шақыру",
            message = "Өтінім қабылданды. Дәрігер сізбен хабарласады.",
            userId = patient.id
        )

        return mapToDto(saved)
    }

    fun getRequestsForPatient(patientId: Long): List<HomeDoctorResponseDto> {
        val patient = patientRepository.findById(patientId)
            .orElseThrow { PatientNotFoundException("Пациент табылмады (ID: $patientId)") }
        return homeDoctorRequestRepository.findByPatientOrderByCreatedAtDesc(patient)
            .map { mapToDto(it) }
    }

    fun cancelRequest(requestId: Long) {
        val request = homeDoctorRequestRepository.findById(requestId)
            .orElseThrow { IllegalArgumentException("Өтінім табылмады") }
        request.status = HomeDoctorStatus.CANCELLED
        request.updatedAt = System.currentTimeMillis()
        homeDoctorRequestRepository.save(request)
        notificationService.createSystemNotification(
            title = "Үйге дәрігер шақыру",
            message = "Өтінім болдырылмады.",
            userId = request.patient?.id
        )
    }

    private fun mapToDto(request: HomeDoctorRequest): HomeDoctorResponseDto {
        return HomeDoctorResponseDto(
            id = request.id ?: 0L,
            patientId = request.patient?.id ?: 0L,
            symptoms = request.symptoms.split(",").filter { it.isNotBlank() },
            address = request.address,
            status = request.status.displayName,
            createdAt = request.createdAt
        )
    }
}
