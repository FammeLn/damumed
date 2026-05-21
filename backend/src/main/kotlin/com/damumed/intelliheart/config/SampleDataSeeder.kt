package com.damumed.intelliheart.config

import com.damumed.intelliheart.entity.Doctor
import com.damumed.intelliheart.repository.DoctorRepository
import org.slf4j.LoggerFactory
import org.springframework.boot.CommandLineRunner
import org.springframework.stereotype.Component

@Component
class SampleDataSeeder(
    private val doctorRepository: DoctorRepository
) : CommandLineRunner {
    private val logger = LoggerFactory.getLogger(javaClass)

    override fun run(vararg args: String?) {
        if (doctorRepository.count() > 0) {
            return
        }

        val doctor = Doctor(
            fullName = "Айдын Серікұлы",
            specialization = "Кардиолог",
            qualification = "Жоғары санат",
            experienceYears = 12,
            licenseNumber = "KZ-CRD-001",
            phoneNumber = "+77010000000",
            email = "aidyn.cardiolog@clinic.kz",
            workplace = "Damumed Clinic",
            rating = 4.7,
            ratingCount = 12,
            isActive = true
        )

        doctorRepository.save(doctor)
        logger.info("Sample doctor created: {}", doctor.fullName)
    }
}
