package com.damumed.intelliheart.repository

import com.damumed.intelliheart.entity.HomeDoctorRequest
import com.damumed.intelliheart.entity.Patient
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface HomeDoctorRequestRepository : JpaRepository<HomeDoctorRequest, Long> {
    fun findByPatientOrderByCreatedAtDesc(patient: Patient): List<HomeDoctorRequest>
}
