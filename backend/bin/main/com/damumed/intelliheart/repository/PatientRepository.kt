package com.damumed.intelliheart.repository

import com.damumed.intelliheart.entity.Patient
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

/**
 * Репозиторий для работы с сущностью Пациент
 * Предоставляет методы для выполнения CRUD операций и пользовательских запросов
 */
@Repository
interface PatientRepository : JpaRepository<Patient, Long> {

    /**
     * Поиск пациента по индивидуальному идентификационному номеру (ИИН)
     */
    fun findByIin(iin: String): Optional<Patient>

    /**
     * Поиск пациента по номеру телефона
     * Используется при авторизации по номеру телефона
     */
    fun findByPhoneNumber(phoneNumber: String): Optional<Patient>

    /**
     * Поиск всех активных пациентов
     */
    fun findAllByIsActive(isActive: Boolean): List<Patient>

    /**
     * Поиск пациента по полному имени (частичное совпадение)
     */
    fun findByFullNameContainingIgnoreCase(fullName: String): List<Patient>

    /**
     * Проверка наличия пациента с данным номером телефона
     */
    fun existsByPhoneNumber(phoneNumber: String): Boolean

    /**
     * Проверка наличия пациента с данным ИИН
     */
    fun existsByIin(iin: String): Boolean
}
