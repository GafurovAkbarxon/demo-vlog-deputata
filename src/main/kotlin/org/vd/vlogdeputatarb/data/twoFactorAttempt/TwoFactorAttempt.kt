package org.vd.vlogdeputatarb.data.twoFactorAttempt

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import java.time.LocalDateTime

@Entity
@Table(
    name = "two_factor_attempts",
    uniqueConstraints = [UniqueConstraint(columnNames = ["username"])]
)
class TwoFactorAttempt(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long? = null,

    @Column(nullable = false, unique = true, length = 100)
    var username: String,

    @Column(nullable = false)
    var failures: Int = 0,

    var lockedUntil: LocalDateTime? = null,

    @Column(nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
)