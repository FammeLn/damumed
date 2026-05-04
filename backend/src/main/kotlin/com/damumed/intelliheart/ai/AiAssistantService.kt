package com.damumed.intelliheart.ai

import com.damumed.intelliheart.dto.AssistantAction
import com.damumed.intelliheart.dto.AssistantRequest
import com.damumed.intelliheart.dto.AssistantResponse
import org.springframework.stereotype.Service

/**
 * Сервис голосового помощника с логикой обработки естественного языка
 * Использует простую симуляцию NLP на основе ключевых слов (в будущем можно интегрировать реальную нейросеть)
 */
@Service
class AiAssistantService {

    /**
     * Обработать запрос от пользователя и вернуть ответ
     * Анализирует текст на основе ключевых слов и определяет интент
     *
     * @param request запрос с распознанным текстом
     * @return ответ помощника с текстом и рекомендуемым действием
     */
    fun processQuery(request: AssistantRequest): AssistantResponse {
        // Приводим текст в нижний регистр для анализа
        val lowerText = request.text.lowercase()

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
            "confidence" to (if (foundKeywords.isNotEmpty()) 0.85 else 0.0)
        )
    }
}
