package org.vd.vlogdeputatarb.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.scheduling.annotation.Async
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.vd.vlogdeputatarb.controller.admin.user.AdminLoginEventResponse
import org.vd.vlogdeputatarb.data.loginEvent.LoginEvent
import org.vd.vlogdeputatarb.data.loginEvent.LoginEventRepository
import java.time.LocalDateTime

@Service
class LoginEventService(
    private val repo: LoginEventRepository
) {

    @Transactional
    @Async
    fun log(event: LoginEvent) {
        repo.save(event)
    }


    fun getEvents(
        spec: Specification<LoginEvent>,
        pageable: Pageable
    ): Page<AdminLoginEventResponse> {
        return repo.findAll(spec, pageable).map {it->
            AdminLoginEventResponse(
                id = it.id!!,
                username = it.username,
                ip = it.ip,
                success = it.success,
                provider = it.provider,
                browser = it.browser,
                os = it.os,
                device = it.device,
                city = it.city,
                country = it.country,
                createdAt = it.createdAt,
                asn=it.asn,
                providerIp=it.providerIp
            )
        }
    }

    fun suspiciousIps(
        minutes: Long = 10,
        threshold: Long = 10
    ): Set<String> {

        val since = LocalDateTime.now().minusMinutes(minutes)

        return repo.findSuspiciousIps(since, threshold)
            .toSet()
    }

    fun browsers(): List<String> =
        repo.findDistinctBrowsers().sorted()

    fun osList(): List<String> =
        repo.findDistinctOs().sorted()

    fun devices(): List<String> =
        repo.findDistinctDevices().sorted()



    data class LoginStats(
        val total: Long,
        val success: Long,
        val fail: Long,
        val uniqueIps: Long
    )
//sql optimized  один агрегатный в репозиториии
    fun stats(): LoginStats {
        return LoginStats(
            total = repo.countAllEvents(),
            success = repo.countSuccess(),
            fail = repo.countFail(),
            uniqueIps = repo.countUniqueIps()
        )
    }
}