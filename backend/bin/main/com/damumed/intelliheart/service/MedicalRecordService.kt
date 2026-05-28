package com.damumed.intelliheart.service

import com.damumed.intelliheart.dto.CreateMedicalRecordRequestDto
import com.damumed.intelliheart.dto.MedicalRecordResponseDto
import com.damumed.intelliheart.entity.MedicalRecord
import com.damumed.intelliheart.exception.PatientNotFoundException
import com.damumed.intelliheart.repository.MedicalRecordRepository
import com.damumed.intelliheart.repository.PatientRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class MedicalRecordService(
    private val medicalRecordRepository: MedicalRecordRepository,
    private val patientRepository: PatientRepository
) {
    fun createRecord(request: CreateMedicalRecordRequestDto): MedicalRecordResponseDto {
        val patient = patientRepository.findById(request.patientId)
            .orElseThrow { PatientNotFoundException("Пациент табылмады (ID: ${request.patientId})") }

        val record = MedicalRecord(
            patient = patient,
            doctorName = request.doctorName,
            specialization = request.specialization,
            visitDate = request.visitDate,
            diagnosis = request.diagnosis,
            notes = request.notes
        )

        return mapToDto(medicalRecordRepository.save(record))
    }

    fun getRecordsForPatient(patientId: Long): List<MedicalRecordResponseDto> {
        val patient = patientRepository.findById(patientId)
            .orElseThrow { PatientNotFoundException("Пациент табылмады (ID: $patientId)") }
        return medicalRecordRepository.findByPatientOrderByVisitDateDesc(patient).map { mapToDto(it) }
    }

    private fun mapToDto(record: MedicalRecord): MedicalRecordResponseDto {
        return MedicalRecordResponseDto(
            id = record.id ?: 0L,
            patientId = record.patient?.id ?: 0L,
            doctorName = record.doctorName,
            specialization = record.specialization,
            visitDate = record.visitDate,
            diagnosis = record.diagnosis,
            notes = record.notes
        )
    }
}
