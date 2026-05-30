package com.damumed.intelliheart.repository

import com.damumed.intelliheart.entity.ChatMessage
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface ChatMessageRepository : JpaRepository<ChatMessage, Long> {
    fun findByPatientIdOrderByCreatedAtAsc(patientId: Long): List<ChatMessage>
}
