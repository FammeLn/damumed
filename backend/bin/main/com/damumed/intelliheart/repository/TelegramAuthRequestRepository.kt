package com.damumed.intelliheart.repository

import com.damumed.intelliheart.entity.TelegramAuthRequest
import com.damumed.intelliheart.entity.TelegramAuthStatus
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface TelegramAuthRequestRepository : JpaRepository<TelegramAuthRequest, String> {
    fun findFirstByTelegramChatIdAndStatusOrderByCreatedAtDesc(
        telegramChatId: Long,
        status: TelegramAuthStatus
    ): Optional<TelegramAuthRequest>
}
