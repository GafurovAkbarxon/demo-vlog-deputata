package org.vd.vlogdeputatarb.util

import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.vd.vlogdeputatarb.service.LoginAttemptService

@Component
class ScheduledJobs(
    private val loginAttemptService: LoginAttemptService
) {

    @Scheduled(cron = "0 0 3 * * ?") // каждый день
    fun loginAttemptsCleanup() = loginAttemptService.cleanup()

}