package org.vd.vlogdeputatarb.security.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.Authentication
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler
import org.springframework.stereotype.Component
import org.vd.vlogdeputatarb.data.loginEvent.LoginEvent
import org.vd.vlogdeputatarb.security.dto.Pre2faAuthenticationToken
import org.vd.vlogdeputatarb.security.dto.UserPrincipal
import org.vd.vlogdeputatarb.service.GeoIpService
import org.vd.vlogdeputatarb.service.LoginAttemptService
import org.vd.vlogdeputatarb.service.LoginEventService
import org.vd.vlogdeputatarb.service.SessionService
import ua_parser.Parser


@Component
class CustomAuthSuccessHandler(
    private val loginAttemptService: LoginAttemptService,
    private val loginEventService: LoginEventService,
    private val parser: Parser,
    private val geoIpService: GeoIpService,
    private val sessionService: SessionService

) : SimpleUrlAuthenticationSuccessHandler() {

    override fun onAuthenticationSuccess(
        request: HttpServletRequest,
        response: HttpServletResponse,
        authentication: Authentication
    ) {
        println("CustomAuthSuccessHandler is working")

        if (authentication is Pre2faAuthenticationToken) {
            println("CustomAuthSuccessHandler: [authentication is Pre2faAuthenticationToken] redirect...2FA")
            response.sendRedirect("/auth/2fa")
            return
        }
        println("CustomAuthSuccessHandler: [authentication isNOT Pre2faAuthenticationToken]  SUCCESS")
        val ip = request.remoteAddr
        val username = authentication.name
        loginAttemptService.registerSuccess(ip, username)

        val principal = authentication.principal as UserPrincipal
        val user = principal.user

        val client = parser.parse(request.getHeader("User-Agent"))
        val geo = geoIpService.resolve(ip)

        val loginEvent = LoginEvent(
            username = username,
            ip = ip,
            success = true,
            provider = user.provider,
            browser = "${client.userAgent.family} ${client.userAgent.major}",
            os = "${client.os.family} ${client.os.major}",
            device = client.device.family,
            userAgent = request.getHeader("User-Agent"),
            city = geo.city,
            country = geo.country,
            asn = geo.asn,
            providerIp = geo.providerIp
        )
        loginEventService.log(loginEvent)

        val sessionId = request.session.id
        sessionService.create(user = user, sessionId = sessionId, event = loginEvent)

        super.onAuthenticationSuccess(request, response, authentication)
    }
}