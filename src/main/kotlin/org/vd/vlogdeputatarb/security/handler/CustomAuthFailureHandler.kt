package org.vd.vlogdeputatarb.security.handler

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.DisabledException
import org.springframework.security.authentication.LockedException
import org.springframework.security.core.AuthenticationException
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.stereotype.Component
import org.vd.vlogdeputatarb.data.loginEvent.LoginEvent
import org.vd.vlogdeputatarb.service.GeoIpService
import org.vd.vlogdeputatarb.util.enums.AuthProvider
import org.vd.vlogdeputatarb.service.LoginEventService
import org.vd.vlogdeputatarb.service.LoginAttemptService
import ua_parser.Parser


@Component
class CustomAuthFailureHandler (
    private val loginAttemptService: LoginAttemptService,
    private val loginEventService: LoginEventService,
    private val parser: Parser,
    private val geoIpService: GeoIpService
): SimpleUrlAuthenticationFailureHandler() {



    override fun onAuthenticationFailure(
        request: HttpServletRequest,
        response: HttpServletResponse,
        exception: AuthenticationException
    ) {
        val ip = request.remoteAddr
        val username = request.getParameter("username") ?: ""
        loginAttemptService.registerFailure(ip, username)


        val geo = geoIpService.resolve(ip)
        val client = parser.parse(request.getHeader("User-Agent"))
        val loginEvent = LoginEvent(
            username = username,
            ip = ip,
            success = false,
            provider = AuthProvider.LOCAL,
            browser = "${client.userAgent.family} ${client.userAgent.major}",
            os = "${client.os.family} ${client.os.major}",
            device = client.device.family,
            userAgent = request.getHeader("User-Agent"),
            city=geo.city,
            country=geo.country,
            asn=geo.asn,
            providerIp=geo.providerIp
        )
        loginEventService.log(loginEvent)





        when (exception) {
            is LockedException, is DisabledException ->{
                println("CustomAuthFailureHandler[LockedException,DisabledException]: ${exception.message}")
                response.sendRedirect("/auth/login?locked")
            }
            else -> {
                println("CustomAuthFailureHandler[else]: ${exception.message}")
                val left = loginAttemptService.attemptsLeft(ip, username)
                if (left != null && left in 1..2) {
                    response.sendRedirect("/auth/login?error&left=$left")
                } else {
                    response.sendRedirect("/auth/login?error")
                }
            }
        }


    }


}