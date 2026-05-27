package com.damumed.intelliheart.repository

import com.damumed.intelliheart.entity.MedicalRecord
import com.damumed.intelliheart.entity.Patient
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface MedicalRecordRepository : JpaRepository<MedicalRecord, Long> {
    fun findByPatientOrderByVisitDateDesc(patient: Patient): List<MedicalRecord>
}
