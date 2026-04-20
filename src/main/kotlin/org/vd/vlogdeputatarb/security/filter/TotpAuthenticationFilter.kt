package org.vd.vlogdeputatarb.security.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.BadCredentialsException
import org.springframework.security.authentication.InsufficientAuthenticationException
import org.springframework.security.core.Authentication
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.stereotype.Component
import org.vd.vlogdeputatarb.security.dto.Pre2faAuthenticationToken
import org.vd.vlogdeputatarb.security.dto.TotpAuthenticationToken
import org.vd.vlogdeputatarb.security.dto.UserPrincipal


class TotpAuthenticationFilter(
    authenticationManager: AuthenticationManager,
    private val securityContextRepository: SecurityContextRepository,
    private val sessionStrategy: SessionAuthenticationStrategy
): AbstractAuthenticationProcessingFilter("/auth/2fa/verify") {

    init {
        setAuthenticationManager(authenticationManager)
    }

    override fun attemptAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse
    ): Authentication {
        val currentAuth = SecurityContextHolder.getContext().authentication
        if (currentAuth !is Pre2faAuthenticationToken) {
            println("2FA.Filter: Pre2faAuthenticationToken != auth")
            throw InsufficientAuthenticationException("2FA. Pre2faAuthenticationToken != auth")
        }else{
            println("2FA.Filter: currentAuth is Pre2faAuthenticationToken ")
        }

        val code = request.getParameter("code")
            if(code==null){
                println("2FA.Filter: code==null")
                throw BadCredentialsException("2FA.Filter: code==null")
            }


        val principal = currentAuth.principal as UserPrincipal
        val authRequest = TotpAuthenticationToken(principal, code)

        return authenticationManager.authenticate(authRequest)
    }





    override fun successfulAuthentication(
        request: HttpServletRequest,
        response: HttpServletResponse,
        chain: FilterChain,
        authResult: Authentication
    ) {
        val context = SecurityContextHolder.createEmptyContext()
        context.authentication = authResult
        SecurityContextHolder.setContext(context)

        sessionStrategy.onAuthentication(authResult, request, response)
        securityContextRepository.saveContext(context, request, response)

        super.successfulAuthentication(request, response, chain, authResult)

}
}