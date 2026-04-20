package org.vd.vlogdeputatarb.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.vd.vlogdeputatarb.data.loginAttempt.LoginAttempt
import org.vd.vlogdeputatarb.data.loginAttempt.LoginAttemptRepository
import java.time.LocalDateTime

@Service
class LoginAttemptService(
    private val repo: LoginAttemptRepository
){
    private val MAX_USER_ATTEMPTS = 3
    private val MAX_IP_ATTEMPTS = 10

    //for filter
    @Transactional
    fun isBlocked(ip: String, username: String?): Boolean {
        val now = LocalDateTime.now()

        val userAttempt = repo.findByIpAndUsername(ip, username)
        val ipAttempt = repo.findByIpAndUsername(ip, "__GLOBAL__")

        return isBlocked(userAttempt, now) || isBlocked(ipAttempt, now)
    }


    @Transactional
    fun registerSuccess(ip: String, username: String?) {
        repo.deleteByIpAndUsername(ip, username)
        repo.deleteByIpAndUsername(ip, "__GLOBAL__")
    }


    @Transactional
    fun registerFailure(ip: String, username: String?) {

        val now = LocalDateTime.now()

        val userAttempt = repo.findByIpAndUsername(ip, username)
            ?: LoginAttempt(ip = ip, username = username)

        val ipAttempt = repo.findByIpAndUsername(ip, "__GLOBAL__")
            ?: LoginAttempt(ip = ip, username = "__GLOBAL__")

        if (isBlocked(userAttempt, now) || isBlocked(ipAttempt, now)) {
            return
        }

        userAttempt.attempts++
        ipAttempt.attempts++

        userAttempt.lastAttempt = now
        ipAttempt.lastAttempt = now

        if (userAttempt.attempts >= MAX_USER_ATTEMPTS) {
            applyProgressiveBlock(userAttempt, now)
        }

        if (ipAttempt.attempts >= MAX_IP_ATTEMPTS) {
            applyProgressiveBlock(ipAttempt, now)
        }

        repo.save(userAttempt)
        repo.save(ipAttempt)
    }

    @Transactional
    fun attemptsLeft(ip: String, username: String?): Int? {
        val attempt = repo.findByIpAndUsername(ip, username) ?: return MAX_USER_ATTEMPTS
        if (attempt.blockedUntil != null) return null
        return (MAX_USER_ATTEMPTS - attempt.attempts).coerceAtLeast(0)
    }


    @Transactional
    fun cleanup() {
        val threshold = LocalDateTime.now().minusDays(7)
        repo.deleteOlderThan(threshold)
    }


    private fun isBlocked(attempt: LoginAttempt?, now: LocalDateTime): Boolean {
        return attempt?.blockedUntil?.isAfter(now) == true
    }

    private fun applyProgressiveBlock(attempt: LoginAttempt, now: LocalDateTime) {

        attempt.blockLevel++

        val minutes = when (attempt.blockLevel) {
            1 -> 5L
            2 -> 15L
            3 -> 60L
            else -> 1440L // 24 часа максимум
        }

        attempt.blockedUntil = now.plusMinutes(minutes)
        attempt.attempts = 0
    }

    @Transactional
    fun blockIpManually(ip: String, minutes: Long = 1440) { // 24 часа по умолчанию

        val now = LocalDateTime.now()

        val ipAttempt = repo.findByIpAndUsername(ip, "__GLOBAL__")
            ?: LoginAttempt(ip = ip, username = "__GLOBAL__")

        ipAttempt.blockedUntil = now.plusMinutes(minutes)
        ipAttempt.blockLevel = 999 // чтобы отличать ручную блокировку
        ipAttempt.attempts = 0

        repo.save(ipAttempt)
    }

    @Transactional
    fun unblockIp(ip: String) {
        repo.deleteByIpAndUsername(ip, "__GLOBAL__")
    }
}