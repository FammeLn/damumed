package com.damumed.intelliheart.repository

import com.damumed.intelliheart.entity.AuthSession
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface AuthSessionRepository : JpaRepository<AuthSession, String>
