package org.vd.vlogdeputatarb.security.filter

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import org.vd.vlogdeputatarb.service.SessionService

@Component
class SessionTrackingFilter(
    private val sessionService: SessionService
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        println("SessionTrackingFilter is working")

        request.getSession(false)?.let {
            sessionService.updateLastRequest(it.id)
        }

        filterChain.doFilter(request, response)
    }
}