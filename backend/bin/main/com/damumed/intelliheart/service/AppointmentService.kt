package com.damumed.intelliheart.service

import com.damumed.intelliheart.dto.AppointmentDoctorDto
import com.damumed.intelliheart.dto.AppointmentPatientDto
import com.damumed.intelliheart.dto.AppointmentResponseDto
import com.damumed.intelliheart.dto.CreateAppointmentRequestDto
import com.damumed.intelliheart.entity.Appointment
import com.damumed.intelliheart.entity.AppointmentStatus
import com.damumed.intelliheart.exception.AppointmentConflictException
import com.damumed.intelliheart.exception.AppointmentNotFoundException
import com.damumed.intelliheart.exception.PatientNotFoundException
import com.damumed.intelliheart.exception.ValidationException
import com.damumed.intelliheart.repository.AppointmentRepository
import com.damumed.intelliheart.repository.PatientRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

/**
 * Сервис для работы с записями к врачам
 * Содержит бизнес-логику для операций над записями пациентов
 */
@Service
@Transactional
class AppointmentService(
    private val appointmentRepository: AppointmentRepository,
    private val patientRepository: PatientRepository,
    private val doctorService: DoctorService
) {

    /**
     * Создать новую запись к врачу
     * @param requestDto данные для создания записи
     * @return информация о созданной записи
     * @throws PatientNotFoundException если пациент не найден
     * @throws com.damumed.intelliheart.exception.DoctorNotFoundException если врач не найден
     * @throws AppointmentConflictException если время занято
     * @throws ValidationException если данные неверные
     */
    fun createAppointment(requestDto: CreateAppointmentRequestDto): AppointmentResponseDto {
        // Валидация входных данных
        if (requestDto.patientId <= 0) {
            throw ValidationException("Пациент ID дұрыс емес")
        }
        if (requestDto.doctorId <= 0) {
            throw ValidationException("Дәрігер ID дұрыс емес")
        }
        if (requestDto.appointmentDateTime.isBefore(LocalDateTime.now())) {
            throw ValidationException("Өткен уақытқа өтінім бере алмайсыз")
        }

        // Получаем пациента
        val patient = patientRepository.findById(requestDto.patientId)
            .orElseThrow { PatientNotFoundException("Пациент табылмады (ID: ${requestDto.patientId})") }

        // Получаем врача
        val doctor = doctorService.getDoctorEntityById(requestDto.doctorId)

        // Проверяем конфликт расписания врача
        val hasConflict = appointmentRepository.existsByDoctorAndAppointmentDateTimeAndStatusNot(
            doctor,
            requestDto.appointmentDateTime,
            AppointmentStatus.CANCELLED
        )

        if (hasConflict) {
            throw AppointmentConflictException("Бұл уақыт бос емес. Өтінім өзге уақытты таңдаңыз")
        }

        // Создаем новую запись
        val appointment = Appointment(
            patient = patient,
            doctor = doctor,
            appointmentDateTime = requestDto.appointmentDateTime,
            durationMinutes = requestDto.durationMinutes,
            status = AppointmentStatus.PENDING,
            reasonForVisit = requestDto.reasonForVisit,
            appointmentType = requestDto.appointmentType
        )

        val savedAppointment = appointmentRepository.save(appointment)

        return mapToAppointmentResponseDto(savedAppointment)
    }

    /**
     * Получить все записи пациента
     * @param patientId идентификатор пациента
     * @return список записей пациента
     * @throws PatientNotFoundException если пациент не найден
     */
    fun getPatientAppointments(patientId: Long): List<AppointmentResponseDto> {
        val patient = patientRepository.findById(patientId)
            .orElseThrow { PatientNotFoundException("Пациент табылмады (ID: $patientId)") }

        return appointmentRepository.findByPatientOrderByAppointmentDateTimeDesc(patient)
            .map { appointment -> mapToAppointmentResponseDto(appointment) }
    }

    /**
     * Получить активные записи пациента (статусы: PENDING, RESCHEDULED)
     * @param patientId идентификатор пациента
     * @return список активных записей пациента
     */
    fun getPatientActiveAppointments(patientId: Long): List<AppointmentResponseDto> {
        val patient = patientRepository.findById(patientId)
            .orElseThrow { PatientNotFoundException("Пациент табылмады (ID: $patientId)") }

        val activeStatuses = listOf(AppointmentStatus.PENDING, AppointmentStatus.RESCHEDULED)

        return appointmentRepository.findByPatientAndStatusIn(patient, activeStatuses)
            .map { appointment -> mapToAppointmentResponseDto(appointment) }
    }

    /**
     * Получить записи по статусу пациента
     * @param patientId идентификатор пациента
     * @param status статус записи
     * @return список записей с данным статусом
     */
    fun getPatientAppointmentsByStatus(patientId: Long, status: AppointmentStatus): List<AppointmentResponseDto> {
        val patient = patientRepository.findById(patientId)
            .orElseThrow { PatientNotFoundException("Пациент табылмады (ID: $patientId)") }

        return appointmentRepository.findByPatientAndStatus(patient, status)
            .map { appointment -> mapToAppointmentResponseDto(appointment) }
    }

    /**
     * Получить запись по идентификатору
     * @param appointmentId идентификатор записи
     * @return информация о записи
     * @throws AppointmentNotFoundException если запись не найдена
     */
    fun getAppointmentById(appointmentId: Long): AppointmentResponseDto {
        val appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow { AppointmentNotFoundException("Өтінім табылмады (ID: $appointmentId)") }

        return mapToAppointmentResponseDto(appointment)
    }

    /**
     * Отменить запись
     * @param appointmentId идентификатор записи
     */
    fun cancelAppointment(appointmentId: Long) {
        val appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow { AppointmentNotFoundException("Өтінім табылмады (ID: $appointmentId)") }

        appointment.status = AppointmentStatus.CANCELLED
        appointment.updatedAt = System.currentTimeMillis()

        appointmentRepository.save(appointment)
    }

    /**
     * Завершить запись (отметить как пройденную)
     * @param appointmentId идентификатор записи
     * @param doctorNotes примечания врача
     * @param diagnosis диагноз
     * @param recommendations рекомендации
     */
    fun completeAppointment(
        appointmentId: Long,
        doctorNotes: String? = null,
        diagnosis: String? = null,
        recommendations: String? = null
    ) {
        val appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow { AppointmentNotFoundException("Өтінім табылмады (ID: $appointmentId)") }

        appointment.status = AppointmentStatus.COMPLETED
        appointment.doctorNotes = doctorNotes
        appointment.diagnosis = diagnosis
        appointment.recommendations = recommendations
        appointment.updatedAt = System.currentTimeMillis()

        appointmentRepository.save(appointment)
    }

    /**
     * Перенести запись на другое время
     * @param appointmentId идентификатор записи
     * @param newDateTime новые дата и время
     */
    fun rescheduleAppointment(appointmentId: Long, newDateTime: LocalDateTime) {
        val appointment = appointmentRepository.findById(appointmentId)
            .orElseThrow { AppointmentNotFoundException("Өтінім табылмады (ID: $appointmentId)") }

        if (newDateTime.isBefore(LocalDateTime.now())) {
            throw ValidationException("Өткен уақытқа өтінім ауыстыра алмайсыз")
        }

        // Проверяем конфликт расписания врача
        val hasConflict = appointmentRepository.existsByDoctorAndAppointmentDateTimeAndStatusNot(
            appointment.doctor!!,
            newDateTime,
            AppointmentStatus.CANCELLED
        )

        if (hasConflict) {
            throw AppointmentConflictException("Бұл уақыт бос емес")
        }

        appointment.appointmentDateTime = newDateTime
        appointment.status = AppointmentStatus.RESCHEDULED
        appointment.updatedAt = System.currentTimeMillis()

        appointmentRepository.save(appointment)
    }

    /**
     * Преобразовать объект Appointment в DTO
     */
    private fun mapToAppointmentResponseDto(appointment: Appointment): AppointmentResponseDto {
        return AppointmentResponseDto(
            id = appointment.id!!,
            patient = AppointmentPatientDto(
                id = appointment.patient!!.id!!,
                fullName = appointment.patient.fullName,
                phoneNumber = appointment.patient.phoneNumber
            ),
            doctor = AppointmentDoctorDto(
                id = appointment.doctor!!.id!!,
                fullName = appointment.doctor.fullName,
                specialization = appointment.doctor.specialization,
                phoneNumber = appointment.doctor.phoneNumber,
                workplace = appointment.doctor.workplace
            ),
            appointmentDateTime = appointment.appointmentDateTime!!,
            durationMinutes = appointment.durationMinutes,
            status = appointment.status.displayName,
            reasonForVisit = appointment.reasonForVisit,
            doctorNotes = appointment.doctorNotes,
            diagnosis = appointment.diagnosis,
            recommendations = appointment.recommendations,
            appointmentType = appointment.appointmentType
        )
    }
}
