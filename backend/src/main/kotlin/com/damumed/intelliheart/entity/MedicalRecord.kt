package com.damumed.intelliheart.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "medical_records")
class MedicalRecord(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    val patient: Patient? = null,

    @Column(nullable = false)
    val doctorName: String = "",

    @Column(nullable = false)
    val specialization: String = "",

    @Column(nullable = false)
    val visitDate: LocalDate = LocalDate.now(),

    @Column(columnDefinition = "TEXT")
    val diagnosis: String? = null,

    @Column(columnDefinition = "TEXT")
    val notes: String? = null,

    @Column(nullable = false, updatable = false)
    val createdAt: Long = System.currentTimeMillis()
)
