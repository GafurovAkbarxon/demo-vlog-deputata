package org.vd.vlogdeputatarb.data.activeSession

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import org.vd.vlogdeputatarb.data.user.User

@Repository
interface ActiveSessionRepository : JpaRepository<ActiveSession, Long> {

    fun findBySessionId(sessionId: String): ActiveSession?

    fun findByUserAndExpiredFalse(user: User): List<ActiveSession>

    fun deleteBySessionId(sessionId: String)
}