package org.vd.vlogdeputatarb.security.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler
import org.springframework.stereotype.Component
import org.vd.vlogdeputatarb.service.SessionService

@Component
class CustomLogoutHandler(
    private val sessionService: SessionService
) : LogoutSuccessHandler {

    override fun onLogoutSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication?
    ) {
println("CustomLogoutHandler is working")
        request.getSession(false)?.let {
            sessionService.expire(it.id)
        }

        response.sendRedirect("/ru/home")
    }
}