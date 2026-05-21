package com.damumed.intelliheart.repository

import com.damumed.intelliheart.entity.AppNotification
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface AppNotificationRepository : JpaRepository<AppNotification, Long> {
    @Query(
        "select n from AppNotification n " +
            "where n.userId = :userId or n.userId is null " +
            "order by n.createdAt desc"
    )
    fun findAllForUser(@Param("userId") userId: Long): List<AppNotification>

    fun findAllByUserIdIsNullOrderByCreatedAtDesc(): List<AppNotification>
}
