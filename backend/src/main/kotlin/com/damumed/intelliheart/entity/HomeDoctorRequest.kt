package com.damumed.intelliheart.entity

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table

@Entity
@Table(name = "home_doctor_requests")
class HomeDoctorRequest(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @ManyToOne
    @JoinColumn(name = "patient_id", nullable = false)
    val patient: Patient? = null,

    @Column(nullable = false, columnDefinition = "TEXT")
    val symptoms: String = "",

    @Column(nullable = false, columnDefinition = "TEXT")
    val address: String = "",

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var status: HomeDoctorStatus = HomeDoctorStatus.PENDING,

    @Column(nullable = false, updatable = false)
    val createdAt: Long = System.currentTimeMillis(),

    @Column(nullable = false)
    var updatedAt: Long = System.currentTimeMillis()
)

enum class HomeDoctorStatus(val displayName: String) {
    PENDING("Күтілуде"),
    CONFIRMED("Қабылданды"),
    COMPLETED("Аяқталды"),
    CANCELLED("Болдырылмады")
}
