package com.damumed.intelliheart.entity

import jakarta.persistence.*
import java.time.LocalDate

@Entity
@Table(name = "analyses")
class Analysis(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val patientId: Long = 0,

    @Column(nullable = false)
    val title: String = "",

    @Column(nullable = false)
    val date: LocalDate? = null,

    @Column(nullable = false)
    val clinic: String = "",

    @Column(nullable = false)
    var status: String = "В ОБРАБОТКЕ",

    @Column
    var pdfFilePath: String? = null,

    @Column(nullable = false, updatable = false)
    val createdAt: Long = System.currentTimeMillis()
)
