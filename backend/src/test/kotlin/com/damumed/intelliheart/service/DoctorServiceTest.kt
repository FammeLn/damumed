package com.damumed.intelliheart.service

import com.damumed.intelliheart.dto.CreateDoctorRequestDto
import com.damumed.intelliheart.entity.Doctor
import com.damumed.intelliheart.repository.DoctorRepository
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.mockito.kotlin.any
import org.mockito.kotlin.argumentCaptor
import org.mockito.kotlin.given
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.web.server.ResponseStatusException

@DisplayName("DoctorService - создание врача")
class DoctorServiceTest {

    private val doctorRepository: DoctorRepository = mock()
    private val doctorService = DoctorService(doctorRepository)

    @Test
    fun `createDoctor should save and return created doctor`() {
        val request = CreateDoctorRequestDto(
            fullName = "Test Doctor",
            specialization = "Cardiologist",
            qualification = "Senior",
            experienceYears = 8,
            licenseNumber = "LIC-123",
            phoneNumber = "+77001234567",
            email = "doctor@test.kz",
            workplace = "City Clinic"
        )

        given(doctorRepository.existsByLicenseNumber(request.licenseNumber)).willReturn(false)
        given(doctorRepository.existsByEmail(request.email)).willReturn(false)
        given(doctorRepository.save(any<Doctor>())).willReturn(
            Doctor(
                id = 11L,
                fullName = request.fullName,
                specialization = request.specialization,
                qualification = request.qualification,
                experienceYears = request.experienceYears,
                rating = 0.0,
                licenseNumber = request.licenseNumber,
                phoneNumber = request.phoneNumber,
                email = request.email,
                workplace = request.workplace,
                ratingCount = 0,
                isActive = true
            )
        )

        val result = doctorService.createDoctor(request)

        val doctorCaptor = argumentCaptor<Doctor>()
        verify(doctorRepository).save(doctorCaptor.capture())
        assertEquals(11L, result.id)
        assertEquals("Test Doctor", doctorCaptor.firstValue.fullName)
    }

    @Test
    fun `createDoctor should fail for duplicate email`() {
        val request = CreateDoctorRequestDto(
            fullName = "Test Doctor",
            specialization = "Cardiologist",
            qualification = "Senior",
            experienceYears = 8,
            licenseNumber = "LIC-123",
            phoneNumber = "+77001234567",
            email = "doctor@test.kz",
            workplace = "City Clinic"
        )

        given(doctorRepository.existsByLicenseNumber(request.licenseNumber)).willReturn(false)
        given(doctorRepository.existsByEmail(request.email)).willReturn(true)

        assertThrows<ResponseStatusException> {
            doctorService.createDoctor(request)
        }
    }
}
