package org.vd.vlogdeputatarb.security.provider

import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.LockedException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.springframework.web.context.request.RequestContextHolder
import org.springframework.web.context.request.ServletRequestAttributes
import org.vd.vlogdeputatarb.security.dto.Pre2faAuthenticationToken
import org.vd.vlogdeputatarb.security.service.CustomLocalUserService
import org.vd.vlogdeputatarb.security.dto.UserPrincipal
import org.vd.vlogdeputatarb.service.LoginAttemptService
import org.vd.vlogdeputatarb.util.enums.RoleType

@Component
class CustomAuthenticationProvider(
    private val userDetailsService: CustomLocalUserService,
    private val passwordEncoder: PasswordEncoder,
    private val loginAttemptService: LoginAttemptService
) : AuthenticationProvider {

    override fun authenticate(authentication: Authentication): Authentication {
        val username = authentication.name
        val password = authentication.credentials?.toString() ?: ""

        val reqAttr = RequestContextHolder.getRequestAttributes() as? ServletRequestAttributes
        val ip = reqAttr?.request?.remoteAddr ?: "unknown"

        if (loginAttemptService.isBlocked(ip, username)) {
            println("1FA.Provider: loginAttemptService.isBlocked(ip, username)")
            throw LockedException("1FA.Provider: loginAttemptService.isBlocked(ip, username)")
        }

        val principal: UserPrincipal = try {
            userDetailsService.loadUserByUsername(username) as UserPrincipal
        } catch (ex: UsernameNotFoundException) {
            println("1FA.Provider: UsernameNotFoundException")
            throw BadCredentialsException("1FA.Provider: UsernameNotFoundException")
        }

        if (!passwordEncoder.matches(password, principal.password)) {
            println("1FA.Provider: password is wrong!!!")
            throw BadCredentialsException("1FA.Provider: password is wrong!!!")
        }


        if (principal.user.blocked) {
            println("1FA.Provider: user.blocked")
            throw BadCredentialsException("1FA.Provider: user.blocked")
        }

        // только admin + включена 2FA -> pre2fa
        if (principal.user.role == RoleType.ADMIN && principal.user.twoFactorEnabled) {
            return Pre2faAuthenticationToken(principal)
        }

        // обычный полный вход
        return UsernamePasswordAuthenticationToken(
            principal,
            null,
            principal.authorities
        )
    }

    override fun supports(authentication: Class<*>): Boolean {
        return UsernamePasswordAuthenticationToken::class.java.isAssignableFrom(authentication)
    }
}