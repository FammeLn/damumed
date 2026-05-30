package com.damumed.intelliheart.service

import com.damumed.intelliheart.dto.CreatePatientRequestDto
import com.damumed.intelliheart.dto.PatientDto
import com.damumed.intelliheart.entity.Patient
import com.damumed.intelliheart.repository.PatientRepository
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
@Transactional
class PatientService(
    private val patientRepository: PatientRepository
) {
    fun createPatient(request: CreatePatientRequestDto): PatientDto {
        val iin = request.iin.trim()
        val fullName = request.fullName.trim()
        val gender = request.gender.trim()
        val phoneNumber = request.phoneNumber.trim()
        val address = request.address.trim()

        if (iin.isBlank() || fullName.isBlank() || gender.isBlank() || phoneNumber.isBlank() || address.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Пациент деректері толық емес")
        }

        val existingPatientByIin = patientRepository.findByIin(iin)
        val existingPatientByPhone = patientRepository.findByPhoneNumber(phoneNumber)

        val patientToUpdate = if (existingPatientByIin.isPresent) {
            existingPatientByIin.get()
        } else if (existingPatientByPhone.isPresent && request.primaryPatientId == null) {
            existingPatientByPhone.get()
        } else {
            null
        }

        if (patientToUpdate != null) {
            val targetIin = if (patientToUpdate.iin != iin && !patientRepository.existsByIin(iin)) {
                iin
            } else {
                patientToUpdate.iin
            }

            val targetPhoneNumber = if (patientToUpdate.phoneNumber != phoneNumber && !patientRepository.existsByPhoneNumber(phoneNumber)) {
                phoneNumber
            } else {
                patientToUpdate.phoneNumber
            }

            val updatedPatient = Patient(
                id = patientToUpdate.id,
                iin = targetIin,
                fullName = fullName,
                dateOfBirth = request.dateOfBirth,
                gender = gender,
                phoneNumber = targetPhoneNumber,
                medicalHistory = request.medicalHistory ?: patientToUpdate.medicalHistory,
                address = address,
                isActive = patientToUpdate.isActive,
                primaryPatientId = patientToUpdate.primaryPatientId ?: request.primaryPatientId,
                relationType = patientToUpdate.relationType ?: request.relationType,
                createdAt = patientToUpdate.createdAt,
                updatedAt = System.currentTimeMillis(),
                userId = patientToUpdate.userId ?: request.userId
            )
            return mapToDto(patientRepository.save(updatedPatient))
        }

        // Для членов семьи телефон может совпадать, сгенерируем уникальный для БД
        var finalPhoneNumber = phoneNumber
        if (request.primaryPatientId != null && patientRepository.existsByPhoneNumber(phoneNumber)) {
            finalPhoneNumber = phoneNumber + "_" + System.currentTimeMillis()
        } else if (patientRepository.existsByPhoneNumber(phoneNumber)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Бұл телефон бойынша пациент бар")
        }

        val patient = Patient(
            iin = iin,
            fullName = fullName,
            dateOfBirth = request.dateOfBirth,
            gender = gender,
            phoneNumber = finalPhoneNumber,
            medicalHistory = request.medicalHistory,
            address = address,
            isActive = true,
            primaryPatientId = request.primaryPatientId,
            relationType = request.relationType,
            userId = request.userId
        )

        return mapToDto(patientRepository.save(patient))
    }

    fun getPatientById(id: Long): PatientDto {
        val patient = patientRepository.findById(id)
            .orElseThrow { ResponseStatusException(HttpStatus.NOT_FOUND, "Пациент табылмады") }
        return mapToDto(patient)
    }

    fun getFamilyMembers(primaryId: Long): List<PatientDto> {
        val members = patientRepository.findByPrimaryPatientId(primaryId)
        return members.map { mapToDto(it) }
    }

    fun createFamilyMember(primaryId: Long, request: CreatePatientRequestDto): PatientDto {
        if (!patientRepository.existsById(primaryId)) {
            throw ResponseStatusException(HttpStatus.NOT_FOUND, "Негізгі пациент табылмады")
        }

        val familyRequest = request.copy(
            primaryPatientId = primaryId
        )

        return createPatient(familyRequest)
    }

    private fun mapToDto(patient: Patient): PatientDto {
        return PatientDto(
            id = patient.id ?: 0L,
            iin = patient.iin,
            fullName = patient.fullName,
            dateOfBirth = patient.dateOfBirth ?: throw ResponseStatusException(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Пациент туған күні бос"
            ),
            gender = patient.gender,
            phoneNumber = patient.phoneNumber,
            address = patient.address,
            medicalHistory = patient.medicalHistory,
            isActive = patient.isActive,
            primaryPatientId = patient.primaryPatientId,
            relationType = patient.relationType,
            userId = patient.userId
        )
    }
}
