package com.damumed.intelliheart.repository

import com.damumed.intelliheart.entity.Doctor
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.Optional

/**
 * Репозиторий для работы с сущностью Врач
 * Предоставляет методы для выполнения CRUD операций и пользовательских запросов
 */
@Repository
interface DoctorRepository : JpaRepository<Doctor, Long> {

    /**
     * Поиск врача по номеру лицензии
     */
    fun findByLicenseNumber(licenseNumber: String): Optional<Doctor>

    /**
     * Поиск врача по email
     */
    fun findByEmail(email: String): Optional<Doctor>

    /**
     * Поиск всех врачей по специализации
     */
    fun findBySpecializationIgnoreCase(specialization: String): List<Doctor>

    /**
     * Поиск всех активных врачей
     */
    fun findAllByIsActive(isActive: Boolean): List<Doctor>

    /**
     * Поиск врачей по специализации и активности
     */
    fun findBySpecializationIgnoreCaseAndIsActive(specialization: String, isActive: Boolean): List<Doctor>

    /**
     * Поиск врача по полному имени (частичное совпадение)
     */
    fun findByFullNameContainingIgnoreCase(fullName: String): List<Doctor>

    /**
     * Поиск всех врачей, отсортированных по рейтингу (по убыванию)
     */
    fun findAllByIsActiveOrderByRatingDesc(isActive: Boolean): List<Doctor>

    /**
     * Поиск врачей по специализации, отсортированных по рейтингу
     */
    fun findBySpecializationIgnoreCaseAndIsActiveOrderByRatingDesc(
        specialization: String,
        isActive: Boolean
    ): List<Doctor>

    /**
     * Проверка наличия врача с данным номером лицензии
     */
    fun existsByLicenseNumber(licenseNumber: String): Boolean

    /**
     * Проверка наличия врача с данным email
     */
    fun existsByEmail(email: String): Boolean
}
