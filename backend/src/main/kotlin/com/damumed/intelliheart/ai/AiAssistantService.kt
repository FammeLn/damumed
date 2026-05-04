package com.damumed.intelliheart.ai

import com.damumed.intelliheart.dto.AssistantAction
import com.damumed.intelliheart.dto.AssistantRequest
import com.damumed.intelliheart.dto.AssistantResponse
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import org.springframework.web.client.RestClientException
import org.slf4j.LoggerFactory

/**
 * Сервис голосового помощника с интеграцией ML микросервиса
 * Использует внешний Python микросервис с обученной нейросетью для классификации интентов
 * В случае недоступности микросервиса использует резервную логику на основе ключевых слов
 */
@Service
class AiAssistantService(
    private val restTemplate: RestTemplate
) {
    private val logger = LoggerFactory.getLogger(javaClass)

    @Value("\${ml.service.url:http://localhost:8000}")
    private lateinit var mlServiceUrl: String

    /**
     * Маппинг действий CALL_HOME_DOCTOR в нужный action для фронтенда
     */
    private val actionMapping = mapOf(
        "NAVIGATE_TO_APPOINTMENT" to AssistantAction.NAVIGATE_TO_APPOINTMENT,
        "NAVIGATE_TO_RECORDS" to AssistantAction.NAVIGATE_TO_RECORDS,
        "CALL_DOCTOR" to AssistantAction.CALL_HOME_DOCTOR,
        "NAVIGATE_TO_PROFILE" to AssistantAction.NAVIGATE_TO_PROFILE,
        "NONE" to AssistantAction.NONE
    )

    /**
     * Обработать запрос от пользователя и вернуть ответ
     * Попытается использовать ML микросервис, при его недоступности вернёт результат резервной логики
     *
     * @param request запрос с распознанным текстом
     * @return ответ помощника с текстом и рекомендуемым действием
     */
    fun processQuery(request: AssistantRequest): AssistantResponse {
        return try {
            logger.info("Отправка запроса в ML микросервис: $mlServiceUrl/predict")
            
            // Создаём запрос для микросервиса
            val mlRequest = MLServiceRequest(text = request.text)
            
            // Вызываем микросервис
            val mlResponse = restTemplate.postForObject(
                "$mlServiceUrl/predict",
                mlRequest,
                MLServiceResponse::class.java
            )
            
            if (mlResponse != null) {
                logger.info("Получен ответ от ML: action=${mlResponse.action}")
                
                // Преобразуем ответ микросервиса в наш формат
                AssistantResponse(
                    text = mlResponse.text,
                    action = actionMapping[mlResponse.action] ?: AssistantAction.NONE
                )
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
     * Резервная логика на основе ключевых слов (используется при недоступности ML микросервиса)
     * Анализирует текст на основе ключевых слов и определяет интент
     *
     * @param text текст для анализа
     * @return ответ помощника с текстом и рекомендуемым действием
     */
    private fun getDefaultResponse(text: String): AssistantResponse {
        val lowerText = text.lowercase()

        // Анализируем текст на наличие ключевых слов для определения интента
        return when {
            // Интент: запись к врачу
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

            // Интент: просмотр медицинской карты
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

            // Интент: вызов врача на дом
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

            // Интент: профиль пользователя
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

            // Иначе - ответ по умолчанию (не понял)
            else -> {
                AssistantResponse(
                    text = "Кешіріңіз, мен сізді түсінбедім. Сұрағыңызды қайталаңызшы. Мысалы: 'дәрігерге жазылу' немесе 'медициналық картамды қарау'.",
                    action = AssistantAction.NONE
                )
            }
        }
    }

    /**
     * Получить расширенный анализ текста (для отладки)
     * Возвращает информацию о найденных ключевых словах
     */
    fun analyzeText(text: String): Map<String, Any> {
        val lowerText = text.lowercase()

        // Ключевые слова для каждого интента
        val intentKeywords = mapOf(
            "APPOINTMENT" to listOf("жазылу", "дәрігер", "врач", "прием", "запись", "қабылдау"),
            "RECORDS" to listOf("анализ", "медкарта", "медициналық карта", "талдау", "история", "өткеннің"),
            "CALL_HOME" to listOf("үйге", "шақыру", "домой", "вызов", "на дом"),
            "PROFILE" to listOf("профиль", "жеке кабинет", "аккаунт", "өзімнің", "мои данные")
        )

        // Подсчитываем найденные ключевые слова по категориям
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

/**
 * Запрос к ML микросервису
 */
data class MLServiceRequest(
    val text: String
)

/**
 * Ответ от ML микросервиса
 */
data class MLServiceResponse(
    val text: String,
    val action: String
)
