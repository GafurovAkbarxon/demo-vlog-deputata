package org.vd.vlogdeputatarb.security.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component

@Component
class TotpSuccessHandler(
    private val commonSuccessHandler: CustomAuthSuccessHandler
) : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {

        println("TotpSuccessHandler is working")

        commonSuccessHandler.onAuthenticationSuccess(request, response, authentication)

    }
}