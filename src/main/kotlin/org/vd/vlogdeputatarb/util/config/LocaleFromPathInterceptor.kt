package org.vd.vlogdeputatarb.util.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor
import org.springframework.web.servlet.LocaleResolver
import java.util.Locale

@Component
class LocaleFromPathInterceptor(
    private val localeResolver: LocaleResolver
) : HandlerInterceptor {

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {

        val path = request.requestURI
        val lang = path.split("/")
            .firstOrNull { it == "ru" || it == "uz" }

        if (lang != null) {
            localeResolver.setLocale(request, response, Locale(lang))
        }

        return true
    }
}