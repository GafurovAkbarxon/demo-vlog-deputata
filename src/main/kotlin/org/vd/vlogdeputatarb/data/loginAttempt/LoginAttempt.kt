package org.vd.vlogdeputatarb.data.loginAttempt

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.vd.vlogdeputatarb.util.enums.BaseModel
import java.time.LocalDateTime

@Entity
@Table(
    name = "login_attempts",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["ip", "username"])
    ]
)
class LoginAttempt(


    @Column(nullable = false)
    var ip: String,

    @Column(nullable = true)
    var username: String?,

    @Column(nullable = false)
    var attempts: Int = 0,

    @Column(nullable = false)
    var lastAttempt: LocalDateTime = LocalDateTime.now(),
    @Column
var blockedUntil: LocalDateTime? = null,
    @Column
    var blockLevel: Int = 0
): BaseModel()