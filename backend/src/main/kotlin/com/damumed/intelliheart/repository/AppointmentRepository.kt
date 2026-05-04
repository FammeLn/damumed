package com.damumed.intelliheart.repository

import com.damumed.intelliheart.entity.Appointment
import com.damumed.intelliheart.entity.AppointmentStatus
import com.damumed.intelliheart.entity.Doctor
import com.damumed.intelliheart.entity.Patient
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

/**
 * Репозиторий для работы с сущностью Запись к врачу
 * Предоставляет методы для выполнения CRUD операций и пользовательских запросов
 */
@Repository
interface AppointmentRepository : JpaRepository<Appointment, Long> {

    /**
     * Поиск всех записей пациента
     */
    fun findByPatient(patient: Patient): List<Appointment>

    /**
     * Поиск всех записей пациента с определённым статусом
     */
    fun findByPatientAndStatus(patient: Patient, status: AppointmentStatus): List<Appointment>

    /**
     * Поиск всех записей врача
     */
    fun findByDoctor(doctor: Doctor): List<Appointment>

    /**
     * Поиск всех записей врача с определённым статусом
     */
    fun findByDoctorAndStatus(doctor: Doctor, status: AppointmentStatus): List<Appointment>

    /**
     * Поиск всех записей врача в определённую дату
     */
    fun findByDoctorAndAppointmentDateTimeBetween(
        doctor: Doctor,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime
    ): List<Appointment>

    /**
     * Поиск всех записей пациента в период времени
     */
    fun findByPatientAndAppointmentDateTimeBetween(
        patient: Patient,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime
    ): List<Appointment>

    /**
     * Поиск всех ожидающих записей для врача
     */
    fun findByDoctorAndStatus(doctor: Doctor, status: String): List<Appointment>

    /**
     * Поиск записей по статусу
     */
    fun findByStatus(status: AppointmentStatus): List<Appointment>

    /**
     * Поиск всех записей, где дата приема прошла (для архивирования)
     */
    fun findByAppointmentDateTimeBeforeAndStatus(
        appointmentDateTime: LocalDateTime,
        status: AppointmentStatus
    ): List<Appointment>

    /**
     * Проверка наличия конфликта расписания врача
     * Проверяет, есть ли уже запись в это время
     */
    fun existsByDoctorAndAppointmentDateTimeAndStatusNot(
        doctor: Doctor,
        appointmentDateTime: LocalDateTime,
        status: AppointmentStatus
    ): Boolean

    /**
     * Поиск всех активных (ожидающих или назначенных) записей для пациента
     */
    fun findByPatientAndStatusIn(patient: Patient, statuses: List<AppointmentStatus>): List<Appointment>

    /**
     * Поиск последних записей пациента
     */
    fun findByPatientOrderByAppointmentDateTimeDesc(patient: Patient): List<Appointment>

    /**
     * Подсчет количества записей врача в определённый период
     */
    fun countByDoctorAndAppointmentDateTimeBetween(
        doctor: Doctor,
        startDateTime: LocalDateTime,
        endDateTime: LocalDateTime
    ): Long
}
