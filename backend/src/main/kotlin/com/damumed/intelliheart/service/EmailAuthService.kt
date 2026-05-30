package com.damumed.intelliheart.service

import com.damumed.intelliheart.dto.EmailAuthRequest
import com.damumed.intelliheart.dto.EmailAuthResponse
import com.damumed.intelliheart.entity.UserAccount
import com.damumed.intelliheart.entity.UserSession
import com.damumed.intelliheart.repository.UserAccountRepository
import com.damumed.intelliheart.repository.UserSessionRepository
import com.damumed.intelliheart.repository.PatientRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
@Transactional
class EmailAuthService(
    private val userAccountRepository: UserAccountRepository,
    private val userSessionRepository: UserSessionRepository,
    private val patientRepository: PatientRepository
) {
    private val passwordEncoder = BCryptPasswordEncoder()

    @Value("\${auth.session-ttl-hours:168}")
    private var sessionTtlHours: Long = 168

    fun register(request: EmailAuthRequest): EmailAuthResponse {
        val email = request.email.trim()
        val password = request.password

        if (email.isBlank() || password.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Email және пароль міндетті")
        }

        if (userAccountRepository.existsByEmailIgnoreCase(email)) {
            throw ResponseStatusException(HttpStatus.CONFLICT, "Бұл email бойынша аккаунт бар")
        }

        val user = UserAccount(
            email = email,
            passwordHash = passwordEncoder.encode(password)
        )

        val savedUser = userAccountRepository.save(user)
        return createSession(savedUser)
    }

    fun login(request: EmailAuthRequest): EmailAuthResponse {
        val email = request.email.trim()
        val password = request.password

        if (email.isBlank() || password.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Email және пароль міндетті")
        }

        val user = userAccountRepository.findByEmailIgnoreCase(email)
            .orElseThrow { ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email немесе пароль қате") }

        if (!passwordEncoder.matches(password, user.passwordHash)) {
            throw ResponseStatusException(HttpStatus.UNAUTHORIZED, "Email немесе пароль қате")
        }

        return createSession(user)
    }

    private fun createSession(user: UserAccount): EmailAuthResponse {
        val userId = user.id ?: throw ResponseStatusException(
            HttpStatus.INTERNAL_SERVER_ERROR,
            "Қолданушыны сақтау мүмкін болмады"
        )
        val token = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        val expiresAt = now + sessionTtlHours * 3_600_000

        userSessionRepository.save(
            UserSession(
                token = token,
                userId = userId,
                createdAt = now,
                expiresAt = expiresAt
            )
        )

        val patientId = patientRepository.findByUserId(userId).map { it.id }.orElse(null)

        return EmailAuthResponse(
            userId = userId,
            email = user.email,
            accessToken = token,
            expiresAt = expiresAt,
            patientId = patientId
        )
    }
}
