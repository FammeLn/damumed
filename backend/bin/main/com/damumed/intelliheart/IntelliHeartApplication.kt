package com.damumed.intelliheart

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

/**
 * Главный класс приложения IntelliHeart Backend
 * Стартовая точка для Spring Boot приложения
 */
@SpringBootApplication
class IntelliHeartApplication

fun main(args: Array<String>) {
    runApplication<IntelliHeartApplication>(*args)
}
