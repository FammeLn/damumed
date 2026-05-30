package com.damumed.intelliheart.ai

import com.damumed.intelliheart.dto.AssistantAction
import com.damumed.intelliheart.dto.AssistantRequest
import com.damumed.intelliheart.dto.AssistantResponse
import com.damumed.intelliheart.dto.ChatHistoryItemDto
import com.damumed.intelliheart.dto.ChatMessageResponseDto
import com.damumed.intelliheart.entity.ChatMessage
import com.damumed.intelliheart.repository.ChatMessageRepository
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.RestClientException
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

/**
 * Сервис голосового помощника с интеграцией ML микросервиса и историей чата
 */
@Service
class AiAssistantService(
    private val restTemplate: RestTemplate,
    private val chatMessageRepository: ChatMessageRepository
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Value("\${ml.service.url:http://localhost:8000}")
    private lateinit var mlServiceUrl: String

    private val actionMapping = mapOf(
        "NAVIGATE_TO_APPOINTMENT" to AssistantAction.NAVIGATE_TO_APPOINTMENT,
        "NAVIGATE_TO_RECORDS" to AssistantAction.NAVIGATE_TO_RECORDS,
        "CALL_DOCTOR" to AssistantAction.CALL_HOME_DOCTOR,
        "NAVIGATE_TO_PROFILE" to AssistantAction.NAVIGATE_TO_PROFILE,
        "NONE" to AssistantAction.NONE
    )

    /**
     * Получить историю чата для конкретного пациента
     */
    fun getChatHistory(patientId: Long): List<ChatMessageResponseDto> {
        return chatMessageRepository.findByPatientIdOrderByCreatedAtAsc(patientId).map {
            ChatMessageResponseDto(
                id = it.id,
                patientId = it.patientId,
                sender = it.sender,
                text = it.text,
                createdAt = it.createdAt
            )
        }
    }

    /**
     * Обработать сообщение в чате, сохранить историю и вернуть ответ бота с учетом контекста
     */
    fun processAndSaveChatMessage(patientId: Long, text: String): AssistantResponse {
        // 1. Получаем историю чата до сохранения текущего сообщения
        val history = chatMessageRepository.findByPatientIdOrderByCreatedAtAsc(patientId).map {
            ChatHistoryItemDto(sender = it.sender, text = it.text)
        }

        // 2. Сохраняем сообщение пользователя
        chatMessageRepository.save(
            ChatMessage(
                patientId = patientId,
                sender = "USER",
                text = text
            )
        )

        // 3. Обрабатываем запрос с контекстом истории
        val response = processQuery(AssistantRequest(text, history))

        // 4. Сохраняем ответ бота
        chatMessageRepository.save(
            ChatMessage(
                patientId = patientId,
                sender = "BOT",
                text = response.text
            )
        )

        return response
    }

    /**
     * Обработать запрос от пользователя и вернуть ответ
     */
    fun processQuery(request: AssistantRequest): AssistantResponse {
        // Сначала проверяем Q&A базу частых вопросов (приветствия, график работы, контакты),
        // чтобы вернуть точный ответ без ошибок классификатора ML
        val qaResponse = getSupportQAResponse(request.text)
        if (qaResponse != null) {
            logger.info("Найдено совпадение в Q&A базе для: ${request.text}")
            return qaResponse
        }

        return try {
            logger.info("Отправка запроса в ML микросервис: $mlServiceUrl/predict")
            
            val mlRequest = MLServiceRequest(
                text = request.text,
                history = request.history
            )
            
            val mlResponse = restTemplate.postForObject(
                "$mlServiceUrl/predict",
                mlRequest,
                MLServiceResponse::class.java
            )
            
            if (mlResponse != null) {
                logger.info("Получен ответ от ML: action=${mlResponse.action}")
                
                val action = actionMapping[mlResponse.action] ?: AssistantAction.NONE
                if (action == AssistantAction.NONE) {
                    AssistantResponse(
                        text = mlResponse.text,
                        action = AssistantAction.NONE
                    )
                } else {
                    AssistantResponse(
                        text = mlResponse.text,
                        action = action
                    )
                }
            } else {
                logger.warn("ML сервис вернул null ответ, используем резервную логику")
                getDefaultResponse(request.text)
            }
            
        } catch (e: RestClientException) {
            logger.error("Ошибка подключения к ML микросервису (${e.message}), используем резервную логику")
            getDefaultResponse(request.text)
        } catch (e: Exception) {
            logger.error("Неожиданная ошибка при обращении к ML микросервису: ${e.message}", e)
            getDefaultResponse(request.text)
        }
    }

    /**
     * Q&A база частых вопросов (на казахском и русском)
     */
    private fun getSupportQAResponse(text: String): AssistantResponse? {
        val lowerText = text.lowercase()
        return when {
            // 1. Приветствия
            lowerText.contains("сәлем") || lowerText.contains("салем") || 
            lowerText.contains("привет") || lowerText.contains("здравствуйте") || 
            lowerText.contains("ассалау") -> {
                AssistantResponse(
                    text = "Сәлеметсіз бе! Мен IntelliHeart медициналық көмекшісімін. Сізге қалай көмектесе аламын? Мысалы, менің жұмыс уақытым, байланыс телефондарым туралы сұрай аласыз немесе дәрігерге жазылуға болады.",
                    action = AssistantAction.NONE
                )
            }
            
            // 2. График работы
            lowerText.contains("жұмыс") || lowerText.contains("работы") || 
            lowerText.contains("график") || lowerText.contains("уақыты") || 
            lowerText.contains("время") -> {
                AssistantResponse(
                    text = "Біздің клиника күн сайын демалыссыз сағат 08:00-ден 20:00-ге дейін жұмыс істейді. Сенбі және жексенбі күндері кезекші дәрігерлер қабылдайды.",
                    action = AssistantAction.NONE
                )
            }
            
            // 3. Контакты
            lowerText.contains("байланыс") || lowerText.contains("телефон") || 
            lowerText.contains("номер") || lowerText.contains("байланысу") || 
            lowerText.contains("контакт") || lowerText.contains("адрес") || 
            lowerText.contains("мекенжай") -> {
                AssistantResponse(
                    text = "Байланыс телефоны: +7 (727) 330-00-00. Мекенжайымыз: Алматы қаласы, Әл-Фараби даңғылы, 71. Сондай-ақ қолданба арқылы дәрігерді үйге шақыруға болады.",
                    action = AssistantAction.NONE
                )
            }

            // 4. Вызов врача на дом
            lowerText.contains("үйге") || lowerText.contains("вызов") || 
            lowerText.contains("шақыру") || lowerText.contains("температура") || 
            lowerText.contains("ыстық") || lowerText.contains("ыстығы") || 
            lowerText.contains("ауырып") || lowerText.contains("нашар") -> {
                AssistantResponse(
                    text = "Дәрігерді үйге шақыру бөліміне өтудеміз. Мұнда үй адресі мен симптомдарды толтырып, сұраныс жібере аласыз.",
                    action = AssistantAction.CALL_HOME_DOCTOR
                )
            }

            // 5. Запись к врачу
            lowerText.contains("жазылу") || lowerText.contains("запись") || 
            lowerText.contains("записаться") || lowerText.contains("қабылдау") || 
            lowerText.contains("дәрігер") || lowerText.contains("врач") || 
            lowerText.contains("прием") -> {
                AssistantResponse(
                    text = "Дәрігерге жазылу бөліміне өтудеміз. Қай дәрігерге және қай уақытқа жазылғыңыз келеді?",
                    action = AssistantAction.NAVIGATE_TO_APPOINTMENT
                )
            }

            // 6. Медициналық карта мен талдаулар
            lowerText.contains("анализ") || lowerText.contains("талдау") || 
            lowerText.contains("нәтиже") || lowerText.contains("результат") || 
            lowerText.contains("медкарта") || lowerText.contains("карта") || 
            lowerText.contains("диагноз") || lowerText.contains("история") -> {
                AssistantResponse(
                    text = "Медициналық карта мен талдаулар бөліміне өтудеміз. Мұнда сіздің барлық қорытындыларыңыз бен анализ нәтижелері көрсетілген.",
                    action = AssistantAction.NAVIGATE_TO_RECORDS
                )
            }

            // 7. Профиль және жеке кабинет
            lowerText.contains("бала") || lowerText.contains("отбасы") || 
            lowerText.contains("туыс") || lowerText.contains("семь") || 
            lowerText.contains("профиль") || lowerText.contains("жеке кабинет") || 
            lowerText.contains("кабинет") || lowerText.contains("аккаунт") || 
            lowerText.contains("өзімнің") || lowerText.contains("мои данные") -> {
                AssistantResponse(
                    text = "Сіздің жеке кабинетіңізге өтудеміз. Мұнда отбасы мүшелерін қосуға және профильді редакциялауға болады.",
                    action = AssistantAction.NAVIGATE_TO_PROFILE
                )
            }

            else -> null
        }
    }

    /**
     * Резервная логика на основе ключевых слов (используется при недоступности ML микросервиса)
     */
    private fun getDefaultResponse(text: String): AssistantResponse {
        val qaResponse = getSupportQAResponse(text)
        if (qaResponse != null) return qaResponse

        val lowerText = text.lowercase()
        return when {
            lowerText.contains("үйге") ||
            lowerText.contains("шақыру") ||
            lowerText.contains("домой") ||
            lowerText.contains("вызов") ||
            lowerText.contains("на дом") -> {
                AssistantResponse(
                    text = "Дәрігерді үйге шақыру операциясын өндіргемін. Қай уақытта көмектесуі керек?",
                    action = AssistantAction.CALL_HOME_DOCTOR
                )
            }

            lowerText.contains("жазылу") ||
            lowerText.contains("дәрігер") ||
            lowerText.contains("врач") ||
            lowerText.contains("прием") ||
            lowerText.contains("запись") ||
            lowerText.contains("қабылдау") -> {
                AssistantResponse(
                    text = "Мен сізді дәрігерге жазуға көмектесемін. Қай дәрігерге жазылғыңыз келеді?",
                    action = AssistantAction.NAVIGATE_TO_APPOINTMENT
                )
            }

            lowerText.contains("анализ") ||
            lowerText.contains("медкарта") ||
            lowerText.contains("медициналық карта") ||
            lowerText.contains("талдау") ||
            lowerText.contains("история") ||
            lowerText.contains("өткеннің") -> {
                AssistantResponse(
                    text = "Міне, сіздің соңғы талдауларыңыз бен медициналық картаңыз. Осыларды қарап көріңіз.",
                    action = AssistantAction.NAVIGATE_TO_RECORDS
                )
            }

            lowerText.contains("профиль") ||
            lowerText.contains("жеке кабинет") ||
            lowerText.contains("аккаунт") ||
            lowerText.contains("өзімнің") ||
            lowerText.contains("мои данные") -> {
                AssistantResponse(
                    text = "Сіздің жеке кабинетке өтіп барамын. Осында өзіңіздің деректеріңізді көре аласыз.",
                    action = AssistantAction.NAVIGATE_TO_PROFILE
                )
            }

            else -> {
                AssistantResponse(
                    text = "Кешіріңіз, мен сізді түсінбедім. Сұрағыңызды қайталаңызшы. Мысалы: 'дәрігерге жазылу' немесе 'медициналық картамды қарау'.",
                    action = AssistantAction.NONE
                )
            }
        }
    }

    fun analyzeText(text: String): Map<String, Any> {
        val lowerText = text.lowercase()

        val intentKeywords = mapOf(
            "APPOINTMENT" to listOf("жазылу", "дәрігер", "врач", "прием", "запись", "қабылдау"),
            "RECORDS" to listOf("анализ", "медкарта", "медициналық карта", "талдау", "история", "өткеннің"),
            "CALL_HOME" to listOf("үйге", "шақыру", "домой", "вызов", "на дом"),
            "PROFILE" to listOf("профиль", "жеке кабинет", "аккаунт", "өзімнің", "мои данные")
        )

        val foundKeywords = mutableMapOf<String, List<String>>()

        intentKeywords.forEach { (intent, keywords) ->
            val found = keywords.filter { keyword -> lowerText.contains(keyword) }
            if (found.isNotEmpty()) {
                foundKeywords[intent] = found
            }
        }

        return mapOf(
            "originalText" to text,
            "foundIntents" to foundKeywords,
            "confidence" to (if (foundKeywords.isNotEmpty()) 0.85 else 0.0),
            "mlServiceUrl" to mlServiceUrl
        )
    }
}

data class MLServiceRequest(
    val text: String,
    val history: List<ChatHistoryItemDto>? = null
)

data class MLServiceResponse(
    val text: String,
    val action: String
)

