package org.vd.vlogdeputatarb.data.loginAttempt

import jakarta.persistence.LockModeType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface LoginAttemptRepository : JpaRepository<LoginAttempt, Long> {
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    fun findByIpAndUsername(ip: String, username: String?): LoginAttempt?

    fun deleteByIpAndUsername(ip: String, username: String?)

    @Modifying
    @Query(
        "delete from LoginAttempt la where la.lastAttempt < :threshold"
    )
    fun deleteOlderThan(
        @Param("threshold") threshold: LocalDateTime
    )
}