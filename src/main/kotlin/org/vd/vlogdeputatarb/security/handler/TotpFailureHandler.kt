package org.vd.vlogdeputatarb.security.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component
import org.vd.vlogdeputatarb.security.dto.Pre2faAuthenticationToken
import org.vd.vlogdeputatarb.service.TwoFactorAttemptService
import org.vd.vlogdeputatarb.security.dto.UserPrincipal

@Component
class TotpFailureHandler(
    private val twoFactorAttemptService: TwoFactorAttemptService
) : SimpleUrlAuthenticationFailureHandler() {

    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        val auth = SecurityContextHolder.getContext().authentication
        val pre = auth as? Pre2faAuthenticationToken
        val username = (pre?.principal as? UserPrincipal)?.username


        when (exception) {

            is LockedException ->{
                println("TotpFailureHandler[LockedException ]: ${exception.message}")
                response.sendRedirect("/auth/2fa?locked")
            }


            is InsufficientAuthenticationException ->{
                println("TotpFailureHandler[InsufficientAuthenticationException]: ${exception.message}")
                response.sendRedirect("/auth/2fa?expired")
            }


            else -> {
                println("TotpFailureHandler[else]: : ${exception.message}")
                if (username.isNullOrBlank()) {
                    println("TotpFailureHandler : username.isNullOrBlank() ")
                    response.sendRedirect("/auth/2fa?expired")
                    return
                }

                val left = twoFactorAttemptService.failuresLeft(username)
                if (left != null && left in 1..2) {
                    response.sendRedirect("/auth/2fa?error&left=$left")
                } else {
                    response.sendRedirect("/auth/2fa?error")
                }
        }
        }
    }
}