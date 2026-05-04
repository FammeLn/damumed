package com.damumed.intelliheart.service

import com.damumed.intelliheart.dto.DoctorDto
import com.damumed.intelliheart.entity.Doctor
import com.damumed.intelliheart.exception.DoctorNotFoundException
import com.damumed.intelliheart.repository.DoctorRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

/**
 * Сервис для работы с врачами
 * Содержит бизнес-логику для операций над профилями врачей
 */
@Service
@Transactional
class DoctorService(
    private val doctorRepository: DoctorRepository
) {

    /**
     * Получить список всех активных врачей
     */
    fun getAllActiveDoctors(): List<DoctorDto> {
        return doctorRepository.findAllByIsActive(true)
            .map { doctor -> mapToDoctorDto(doctor) }
    }

    /**
     * Получить список врачей по специализации
     * @param specialization специализация врача
     * @return список активных врачей данной специализации
     */
    fun getDoctorsBySpecialization(specialization: String): List<DoctorDto> {
        return doctorRepository.findBySpecializationIgnoreCaseAndIsActiveOrderByRatingDesc(specialization, true)
            .map { doctor -> mapToDoctorDto(doctor) }
    }

    /**
     * Получить врача по идентификатору
     * @param id идентификатор врача
     * @return информация о враче
     * @throws DoctorNotFoundException если врач не найден
     */
    fun getDoctorById(id: Long): DoctorDto {
        val doctor = doctorRepository.findById(id)
            .orElseThrow { DoctorNotFoundException("Дәрігер табылмады (ID: $id)") }
        return mapToDoctorDto(doctor)
    }

    /**
     * Получить объект Doctor по идентификатору для внутреннего использования
     * @param id идентификатор врача
     * @return объект Doctor
     * @throws DoctorNotFoundException если врач не найден
     */
    fun getDoctorEntityById(id: Long): Doctor {
        return doctorRepository.findById(id)
            .orElseThrow { DoctorNotFoundException("Дәрігер табылмады (ID: $id)") }
    }

    /**
     * Получить список врачей отсортированный по рейтингу
     * @return список врачей в порядке убывания рейтинга
     */
    fun getDoctorsSortedByRating(): List<DoctorDto> {
        return doctorRepository.findAllByIsActiveOrderByRatingDesc(true)
            .map { doctor -> mapToDoctorDto(doctor) }
    }

    /**
     * Получить врача по email
     * @param email email врача
     * @return информация о враче
     * @throws DoctorNotFoundException если врач не найден
     */
    fun getDoctorByEmail(email: String): DoctorDto {
        val doctor = doctorRepository.findByEmail(email)
            .orElseThrow { DoctorNotFoundException("Дәрігер табылмады") }
        return mapToDoctorDto(doctor)
    }

    /**
     * Поиск врачей по имени
     * @param fullName полное имя врача (частичное совпадение)
     * @return список врачей
     */
    fun searchDoctorsByName(fullName: String): List<DoctorDto> {
        return doctorRepository.findByFullNameContainingIgnoreCase(fullName)
            .filter { it.isActive }
            .map { doctor -> mapToDoctorDto(doctor) }
    }

    /**
     * Обновить рейтинг врача
     * @param doctorId идентификатор врача
     * @param newRating новый рейтинг (0-5)
     */
    fun updateDoctorRating(doctorId: Long, newRating: Double) {
        val doctor = getDoctorEntityById(doctorId)
        val currentRating = doctor.rating
        val newRatingCount = doctor.ratingCount + 1

        // Вычисляем средний рейтинг
        val averageRating = ((currentRating * doctor.ratingCount) + newRating) / newRatingCount

        // Ограничиваем рейтинг в диапазон 0-5
        val constrainedRating = averageRating.coerceIn(0.0, 5.0)

        doctor.rating = constrainedRating
        doctor.ratingCount = newRatingCount
        doctor.updatedAt = System.currentTimeMillis()

        doctorRepository.save(doctor)
    }

    /**
     * Преобразовать объект Doctor в DTO
     */
    private fun mapToDoctorDto(doctor: Doctor): DoctorDto {
        return DoctorDto(
            id = doctor.id!!,
            fullName = doctor.fullName,
            specialization = doctor.specialization,
            qualification = doctor.qualification,
            experienceYears = doctor.experienceYears,
            rating = doctor.rating,
            ratingCount = doctor.ratingCount,
            workplace = doctor.workplace,
            phoneNumber = doctor.phoneNumber,
            email = doctor.email
        )
    }
}
