package org.vd.vlogdeputatarb.data.loginEvent

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import java.time.LocalDateTime

interface LoginEventRepository : JpaRepository<LoginEvent, Long>, JpaSpecificationExecutor<LoginEvent> {

    fun findByUsername(username: String, pageable: Pageable): Page<LoginEvent>

    fun findByIp(ip: String, pageable: Pageable): Page<LoginEvent>

    fun findBySuccess(success: Boolean, pageable: Pageable): Page<LoginEvent>



    @Query("select distinct le.browser from LoginEvent le where le.browser is not null")
    fun findDistinctBrowsers(): List<String>

    @Query("select distinct le.os from LoginEvent le where le.os is not null")
    fun findDistinctOs(): List<String>

    @Query("select distinct le.device from LoginEvent le where le.device is not null")
    fun findDistinctDevices(): List<String>

    @Query("""
    select le.ip 
    from LoginEvent le
    where le.success = false
      and le.createdAt >= :since
    group by le.ip
    having count(le) >= :threshold
""")
    fun findSuspiciousIps(
        @Param("since") since: LocalDateTime,
        @Param("threshold") threshold: Long
    ): List<String>


    @Query("select count(le) from LoginEvent le")
    fun countAllEvents(): Long

    @Query("select count(le) from LoginEvent le where le.success = true")
    fun countSuccess(): Long

    @Query("select count(le) from LoginEvent le where le.success = false")
    fun countFail(): Long

    @Query("select count(distinct le.ip) from LoginEvent le")
    fun countUniqueIps(): Long
}