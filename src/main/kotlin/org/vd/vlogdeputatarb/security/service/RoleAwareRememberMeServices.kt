package org.vd.vlogdeputatarb.security.service

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.rememberme.PersistentTokenBasedRememberMeServices
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository
import org.vd.vlogdeputatarb.security.dto.UserPrincipal
import org.vd.vlogdeputatarb.util.enums.RoleType

class RoleAwareRememberMeServices(
    key: String,
    userDetailsService: UserDetailsService,
    tokenRepository: PersistentTokenRepository
) : PersistentTokenBasedRememberMeServices(key, userDetailsService, tokenRepository) {

    override fun createSuccessfulAuthentication(
        request: HttpServletRequest,
        user: UserDetails
    ) = super.createSuccessfulAuthentication(request, user).also {
        val principal = user as? UserPrincipal
        if (principal != null) {
            val isAdmin = principal.user.role == RoleType.ADMIN
            val needs2fa = principal.user.twoFactorEnabled
            if (isAdmin && needs2fa) {
                throw InsufficientAuthenticationException("2FA required")
            }
        }
    }
}