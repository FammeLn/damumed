package com.damumed.intelliheart.repository

import com.damumed.intelliheart.entity.Reminder
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ReminderRepository : JpaRepository<Reminder, Long> {
    fun findByPatientIdOrderByScheduledAtAsc(patientId: Long): List<Reminder>
}
