package org.vd.vlogdeputatarb.data.twoFactorAttempt

import org.springframework.data.jpa.repository.JpaRepository

interface TwoFactorAttemptRepository : JpaRepository<TwoFactorAttempt, Long> {
    fun findByUsername(username: String): TwoFactorAttempt?
    fun deleteByUsername(username: String)
}