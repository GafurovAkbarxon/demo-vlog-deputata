package org.vd.vlogdeputatarb.service

import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.vd.vlogdeputatarb.data.twoFactorAttempt.TwoFactorAttempt
import org.vd.vlogdeputatarb.data.twoFactorAttempt.TwoFactorAttemptRepository
import java.time.LocalDateTime

@Service
class TwoFactorAttemptService(
    private val repo: TwoFactorAttemptRepository
) {
    private val maxFailures = 5
    private val lockMinutes = 10L   //блок на 10 минут
    private val resetWindowMinutes = 30L //если 30 минут не было активности — “окно” попыток сбрасываем

    @Transactional(readOnly = true)
    fun isBlocked(username: String): Boolean {
        val attempt = repo.findByUsername(username)?:return false

        val until = attempt.lockedUntil ?: return false
        return until.isAfter(LocalDateTime.now())
    }

    @Transactional
    fun registerFailure(username: String) {
        val now = LocalDateTime.now()

        val attempt = repo.findByUsername(username)?: TwoFactorAttempt(username = username)



        if (attempt.updatedAt.isBefore(now.minusMinutes(resetWindowMinutes))) {
            attempt.failures = 0
            attempt.lockedUntil = null
        }


        if (attempt.lockedUntil?.isAfter(now) == true) {
            attempt.updatedAt = now
            repo.save(attempt)
            return
        }

        attempt.failures += 1
        attempt.updatedAt = now

        if (attempt.failures >= maxFailures) {
            attempt.lockedUntil = now.plusMinutes(lockMinutes)
        }

        repo.save(attempt)
    }

    @Transactional
    fun registerSuccess(username: String) {

        repo.deleteByUsername(username)
    }

    @Transactional(readOnly = true)
    fun failuresLeft(username: String): Int? {
        val attempt = repo.findByUsername(username)?: return maxFailures
        val now = LocalDateTime.now()
        if (attempt.lockedUntil?.isAfter(now) == true) return 0
        return (maxFailures - attempt.failures).coerceAtLeast(0)//“Если значение меньше указанного минимума — вернуть минимум, иначе вернуть само значение”
    }
}