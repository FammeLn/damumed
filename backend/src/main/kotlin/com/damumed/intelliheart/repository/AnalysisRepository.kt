package com.damumed.intelliheart.repository

import com.damumed.intelliheart.entity.Analysis
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AnalysisRepository : JpaRepository<Analysis, Long> {
    fun findByPatientId(patientId: Long): List<Analysis>
}
