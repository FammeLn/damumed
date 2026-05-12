package com.damumed.intelliheart.service

import com.damumed.intelliheart.dto.AuthPatientDto
import com.damumed.intelliheart.dto.TelegramAuthStartRequest
import com.damumed.intelliheart.dto.TelegramAuthStartResponse
import com.damumed.intelliheart.dto.TelegramAuthStatusResponse
import com.damumed.intelliheart.entity.AuthProvider
import com.damumed.intelliheart.entity.AuthSession
import com.damumed.intelliheart.entity.Patient
import com.damumed.intelliheart.entity.TelegramAuthRequest
import com.damumed.intelliheart.entity.TelegramAuthStatus
import com.damumed.intelliheart.repository.AuthSessionRepository
import com.damumed.intelliheart.repository.PatientRepository
import com.damumed.intelliheart.repository.TelegramAuthRequestRepository
import com.fasterxml.jackson.databind.JsonNode
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.client.RestTemplate
import org.springframework.web.server.ResponseStatusException
import java.util.UUID

@Service
@Transactional
class TelegramAuthService(
    private val patientRepository: PatientRepository,
    private val telegramAuthRequestRepository: TelegramAuthRequestRepository,
    private val authSessionRepository: AuthSessionRepository,
    private val restTemplate: RestTemplate
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Value("\${telegram.bot.token:}")
    private lateinit var botToken: String

    @Value("\${telegram.bot.username:intelliheart_auth_bot}")
    private lateinit var botUsername: String

    @Value("\${telegram.bot.auth-expiration-minutes:10}")
    private var authExpirationMinutes: Long = 10

    @Value("\${telegram.bot.session-ttl-hours:168}")
    private var sessionTtlHours: Long = 168

    fun startAuth(request: TelegramAuthStartRequest): TelegramAuthStartResponse {
        ensureTelegramConfigured()

        val normalizedPhone = normalizePhoneNumber(request.phoneNumber)
        val patient = findPatientByPhone(normalizedPhone)
            ?: throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Пациент с таким номером не найден"
            )

        val now = System.currentTimeMillis()
        val authRequest = TelegramAuthRequest(
            phoneNumber = request.phoneNumber,
            normalizedPhoneNumber = normalizedPhone,
            patientId = patient.id!!,
            status = TelegramAuthStatus.PENDING,
            expiresAt = now + authExpirationMinutes * 60_000
        )
        telegramAuthRequestRepository.save(authRequest)

        return TelegramAuthStartResponse(
            authRequestId = authRequest.id,
            botLink = "https://t.me/$botUsername?start=${authRequest.id}",
            expiresAt = authRequest.expiresAt
        )
    }

    fun getStatus(authRequestId: String): TelegramAuthStatusResponse {
        val request = telegramAuthRequestRepository.findById(authRequestId)
            .orElseThrow {
                ResponseStatusException(HttpStatus.NOT_FOUND, "Сессия Telegram-авторизации не найдена")
            }

        expireIfNeeded(request)
        val patient = patientRepository.findById(request.patientId).orElse(null)
        val isAuthenticated = request.status == TelegramAuthStatus.VERIFIED && !request.accessToken.isNullOrBlank()

        return TelegramAuthStatusResponse(
            authRequestId = request.id,
            status = request.status.name,
            isAuthenticated = isAuthenticated,
            accessToken = if (isAuthenticated) request.accessToken else null,
            patient = if (isAuthenticated && patient != null) {
                AuthPatientDto(
                    id = patient.id!!,
                    fullName = patient.fullName,
                    phoneNumber = patient.phoneNumber
                )
            } else {
                null
            },
            expiresAt = request.expiresAt
        )
    }

    fun handleWebhook(update: JsonNode) {
        if (botToken.isBlank()) {
            return
        }

        val messageNode = update.get("message") ?: return
        val chatId = messageNode.path("chat").path("id").asLong(0L)
        if (chatId == 0L) return

        val fromUserId = messageNode.path("from").path("id").asLong(0L)
        val text = messageNode.path("text").asText("")

        if (text.startsWith("/start")) {
            val authRequestId = text.split(" ").getOrNull(1)
            processStartCommand(chatId, fromUserId, authRequestId)
            return
        }

        val contactNode = messageNode.get("contact") ?: return
        processContact(
            chatId = chatId,
            fromUserId = fromUserId,
            contactUserId = contactNode.path("user_id").asLong(0L),
            contactPhoneNumber = contactNode.path("phone_number").asText("")
        )
    }

    private fun processStartCommand(chatId: Long, fromUserId: Long, authRequestId: String?) {
        if (authRequestId.isNullOrBlank()) {
            sendText(chatId, "Ссылка авторизации некорректна. Запустите вход заново в приложении.")
            return
        }

        val request = telegramAuthRequestRepository.findById(authRequestId).orElse(null)
        if (request == null) {
            sendText(chatId, "Сессия авторизации не найдена. Запустите вход заново.")
            return
        }

        if (request.status != TelegramAuthStatus.PENDING) {
            sendText(chatId, "Эта сессия уже завершена. Запустите новую авторизацию в приложении.")
            return
        }

        if (System.currentTimeMillis() > request.expiresAt) {
            request.status = TelegramAuthStatus.EXPIRED
            telegramAuthRequestRepository.save(request)
            sendText(chatId, "Сессия истекла. Запустите авторизацию заново в приложении.")
            return
        }

        request.telegramChatId = chatId
        request.telegramUserId = fromUserId
        telegramAuthRequestRepository.save(request)

        sendPhoneRequest(chatId)
    }

    private fun processContact(
        chatId: Long,
        fromUserId: Long,
        contactUserId: Long,
        contactPhoneNumber: String
    ) {
        val request = telegramAuthRequestRepository
            .findFirstByTelegramChatIdAndStatusOrderByCreatedAtDesc(chatId, TelegramAuthStatus.PENDING)
            .orElse(null)

        if (request == null) {
            sendText(chatId, "Нет активной сессии авторизации. Начните вход из приложения.")
            return
        }

        if (System.currentTimeMillis() > request.expiresAt) {
            request.status = TelegramAuthStatus.EXPIRED
            telegramAuthRequestRepository.save(request)
            sendText(chatId, "Сессия авторизации истекла. Запустите вход заново.")
            return
        }

        if (contactUserId != 0L && fromUserId != 0L && contactUserId != fromUserId) {
            request.status = TelegramAuthStatus.FAILED
            telegramAuthRequestRepository.save(request)
            sendText(chatId, "Подтверждение отклонено: используйте ваш собственный номер Telegram.")
            return
        }

        val normalizedContactPhone = normalizePhoneNumber(contactPhoneNumber)
        if (normalizedContactPhone != request.normalizedPhoneNumber) {
            request.status = TelegramAuthStatus.FAILED
            telegramAuthRequestRepository.save(request)
            sendText(chatId, "Номер не совпадает с номером аккаунта. Запустите вход заново.")
            return
        }

        val token = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        request.status = TelegramAuthStatus.VERIFIED
        request.verifiedAt = now
        request.accessToken = token
        telegramAuthRequestRepository.save(request)

        authSessionRepository.save(
            AuthSession(
                token = token,
                patientId = request.patientId,
                provider = AuthProvider.TELEGRAM,
                createdAt = now,
                expiresAt = now + sessionTtlHours * 3_600_000
            )
        )

        sendText(chatId, "Номер подтвержден. Возвращайтесь в приложение — вход выполнен.")
    }

    private fun sendPhoneRequest(chatId: Long) {
        val url = "https://api.telegram.org/bot$botToken/sendMessage"
        val payload = mapOf(
            "chat_id" to chatId,
            "text" to "Для входа нажмите кнопку ниже и поделитесь номером телефона.",
            "reply_markup" to mapOf(
                "keyboard" to listOf(
                    listOf(
                        mapOf(
                            "text" to "📱 Поделиться номером",
                            "request_contact" to true
                        )
                    )
                ),
                "resize_keyboard" to true,
                "one_time_keyboard" to true
            )
        )
        runCatching { restTemplate.postForObject(url, payload, Map::class.java) }
            .onFailure { logger.warn("Не удалось отправить кнопку запроса номера в Telegram: ${it.message}") }
    }

    private fun sendText(chatId: Long, text: String) {
        val url = "https://api.telegram.org/bot$botToken/sendMessage"
        val payload = mapOf(
            "chat_id" to chatId,
            "text" to text
        )
        runCatching { restTemplate.postForObject(url, payload, Map::class.java) }
            .onFailure { logger.warn("Не удалось отправить сообщение в Telegram: ${it.message}") }
    }

    private fun findPatientByPhone(normalizedPhone: String): Patient? {
        val direct = patientRepository.findByPhoneNumber(normalizedPhone)
        if (direct.isPresent) return direct.get()

        return patientRepository.findAllByIsActive(true)
            .firstOrNull { normalizePhoneNumber(it.phoneNumber) == normalizedPhone }
    }

    private fun expireIfNeeded(request: TelegramAuthRequest) {
        if (request.status == TelegramAuthStatus.PENDING && System.currentTimeMillis() > request.expiresAt) {
            request.status = TelegramAuthStatus.EXPIRED
            telegramAuthRequestRepository.save(request)
        }
    }

    private fun ensureTelegramConfigured() {
        if (botToken.isBlank()) {
            throw ResponseStatusException(
                HttpStatus.SERVICE_UNAVAILABLE,
                "Telegram авторизация не настроена: укажите TELEGRAM_BOT_TOKEN"
            )
        }
    }

    private fun normalizePhoneNumber(phoneNumber: String): String {
        val digitsOnly = phoneNumber.filter { it.isDigit() }
        if (digitsOnly.isBlank()) {
            throw ResponseStatusException(HttpStatus.BAD_REQUEST, "Неверный формат номера телефона")
        }

        val normalizedDigits = when {
            digitsOnly.length == 11 && digitsOnly.startsWith("8") -> "7${digitsOnly.drop(1)}"
            digitsOnly.length == 10 -> "7$digitsOnly"
            digitsOnly.length == 11 && digitsOnly.startsWith("7") -> digitsOnly
            else -> digitsOnly
        }

        return "+$normalizedDigits"
    }
}
