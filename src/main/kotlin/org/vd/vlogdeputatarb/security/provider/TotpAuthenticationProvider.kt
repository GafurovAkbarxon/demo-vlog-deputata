package org.vd.vlogdeputatarb.security.provider

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.authentication.LockedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import org.vd.vlogdeputatarb.security.dto.TotpAuthenticationToken
import org.vd.vlogdeputatarb.service.TwoFactorAttemptService
import org.vd.vlogdeputatarb.security.dto.UserPrincipal
import org.vd.vlogdeputatarb.service.TotpSecretCrypto
import org.vd.vlogdeputatarb.service.TotpService

@Component
class TotpAuthenticationProvider(
    private val totpService: TotpService,
    private val twoFactorAttemptService: TwoFactorAttemptService,
    private val totpSecretCrypto: TotpSecretCrypto
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        val principal = authentication.principal as UserPrincipal
        val code = authentication.credentials as String

        if (twoFactorAttemptService.isBlocked(principal.username)) {
            println("2FA.Provider: twoFactorAttemptService.isBlocked")
            throw LockedException("2FA.Provider: twoFactorAttemptService.isBlocked")
        }

        val encrypted = principal.user.twoFactorSecret
            if (encrypted==null) {
                println("2FA.Provider: user.encryptedSecret is null")
                throw InsufficientAuthenticationException("2FA.Provider: user.encryptedSecret is null")
            }
        val secret = totpSecretCrypto.decrypt(encrypted)


        val ok = totpService.verifyCode(secret, code)
        if (!ok) {
            twoFactorAttemptService.registerFailure(principal.username)
            println("2FA.Provider: verify code is failed")
            throw BadCredentialsException("2FA.Provider: verify code is failed")
        }

        twoFactorAttemptService.registerSuccess(principal.username)

        return UsernamePasswordAuthenticationToken(principal, null, principal.authorities)
    }

    override fun supports(authentication: Class<*>): Boolean {
        return TotpAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}