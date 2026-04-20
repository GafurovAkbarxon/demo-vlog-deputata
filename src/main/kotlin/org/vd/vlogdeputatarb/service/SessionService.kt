package org.vd.vlogdeputatarb.service

import org.springframework.security.core.session.SessionRegistry
import org.springframework.stereotype.Service
import org.vd.vlogdeputatarb.controller.account.profile.dto.ActiveSessionResponse
import org.vd.vlogdeputatarb.data.activeSession.ActiveSession
import org.vd.vlogdeputatarb.data.activeSession.ActiveSessionRepository
import org.vd.vlogdeputatarb.data.loginEvent.LoginEvent
import org.vd.vlogdeputatarb.data.user.User
import java.time.Instant

@Service
class SessionService(
    private val repository: ActiveSessionRepository,
    private val sessionRegistry: SessionRegistry
) {
    fun create(
        user: User,
        sessionId: String,
        event: LoginEvent
    ) {

        val session = ActiveSession(
            user = user,
            sessionId = sessionId,
            ip = event.ip,
            country = event.country,
            city = event.city,
            providerIp = event.providerIp,
            browser = event.browser,
            os = event.os,
            device = event.device
        )

        repository.save(session)
    }
    fun getUserSessions(user: User, currentSessionId: String): List<ActiveSessionResponse> {
        val rows = repository.findByUserAndExpiredFalse(user)

        val active = mutableListOf<ActiveSessionResponse>()

        for (row in rows) {
            val info = sessionRegistry.getSessionInformation(row.sessionId)

            if (info == null || info.isExpired) {
                row.expired = true
                repository.save(row)
                continue
            }

            active += ActiveSessionResponse(
                sessionId = row.sessionId,
                ip = row.ip,
                country = row.country,
                city = row.city,
                providerIp = row.providerIp,
                browser = row.browser,
                os = row.os,
                device = row.device,
                createdAt = row.createdAt,
                lastRequestAt = row.lastRequestAt,
                current = currentSessionId == row.sessionId
            )
        }

        return active
    }

    fun expire(sessionId: String) {
        sessionRegistry.getSessionInformation(sessionId)?.expireNow()

        // Помечаем в БД
        repository.findBySessionId(sessionId)?.let {
            it.expired = true
            repository.save(it)
        }
    }

    fun expireAllExcept(user: User, currentSessionId: String) {
        repository.findByUserAndExpiredFalse(user)
            .filter { it.sessionId != currentSessionId }
            .forEach {

                sessionRegistry.getSessionInformation(it.sessionId)?.expireNow()

                it.expired = true
                repository.save(it)
            }
    }

    fun updateLastRequest(sessionId: String) {
        repository.findBySessionId(sessionId)?.let {
            it.lastRequestAt = Instant.now()
            repository.save(it)
        }
    }
}