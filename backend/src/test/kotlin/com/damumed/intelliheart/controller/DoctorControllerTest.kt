package com.damumed.intelliheart.controller

import com.damumed.intelliheart.dto.CreateDoctorRequestDto
import com.damumed.intelliheart.dto.DoctorDto
import com.damumed.intelliheart.service.DoctorService
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.given
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.springframework.http.HttpStatus

@DisplayName("DoctorController - REST API")
class DoctorControllerTest {

    private val doctorService: DoctorService = mock()
    private val controller = DoctorController(doctorService)

    @Test
    fun `createDoctor should return 201 and created doctor`() {
        val request = CreateDoctorRequestDto(
            fullName = "Doctor A",
            specialization = "Therapist",
            qualification = "First category",
            experienceYears = 4,
            licenseNumber = "LIC-777",
            phoneNumber = "+77001112233",
            email = "doctor.a@test.kz",
            workplace = "Clinic A"
        )

        val responseBody = DoctorDto(
            id = 1L,
            fullName = request.fullName,
            specialization = request.specialization,
            qualification = request.qualification,
            experienceYears = request.experienceYears,
            rating = 0.0,
            ratingCount = 0,
            workplace = request.workplace,
            phoneNumber = request.phoneNumber,
            email = request.email
        )
        given(doctorService.createDoctor(any())).willReturn(responseBody)

        val response = controller.createDoctor(request)

        assertEquals(HttpStatus.CREATED, response.statusCode)
        assertEquals(1L, response.body?.id)
        verify(doctorService).createDoctor(request)
    }
}
