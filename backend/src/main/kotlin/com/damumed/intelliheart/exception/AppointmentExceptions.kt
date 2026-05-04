package com.damumed.intelliheart.exception

/**
 * Исключение выбрасывается когда врач не найден по идентификатору
 */
class DoctorNotFoundException(message: String = "Дәрігер табылмады") : RuntimeException(message)

/**
 * Исключение выбрасывается когда пациент не найден по идентификатору
 */
class PatientNotFoundException(message: String = "Пациент табылмады") : RuntimeException(message)

/**
 * Исключение выбрасывается когда невозможно создать запись (например, время занято)
 */
class AppointmentConflictException(message: String = "Бұл уақыт бос емес") : RuntimeException(message)

/**
 * Исключение выбрасывается при других ошибках приложения
 */
class AppointmentNotFoundException(message: String = "Өтінім табылмады") : RuntimeException(message)

/**
 * Исключение выбрасывается при ошибке валидации данных
 */
class ValidationException(message: String = "Деректер дұрыс емес") : RuntimeException(message)
