package com.damumed.intelliheart.repository

import com.damumed.intelliheart.entity.UserAccount
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

@Repository
interface UserAccountRepository : JpaRepository<UserAccount, Long> {
    fun findByEmailIgnoreCase(email: String): Optional<UserAccount>

    fun existsByEmailIgnoreCase(email: String): Boolean
}
