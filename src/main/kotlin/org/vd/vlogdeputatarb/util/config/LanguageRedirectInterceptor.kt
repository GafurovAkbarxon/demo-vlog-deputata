package org.vd.vlogdeputatarb.util.config

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.stereotype.Component
import org.springframework.web.servlet.HandlerInterceptor

@Component
class LanguageRedirectInterceptor() : HandlerInterceptor {

    private val supportedLangs = listOf("ru", "uz")

    private val excludedPaths = listOf(
        "/css/", "/js/",
        "/image/", "/uploads/",
        "/robots.txt",
        "/sitemap.xml", "/news-sitemap.xml",
        "/auth/", "/logout",
        "/error","/access-denied", "/favicon.ico",
    )

    override fun preHandle(
        request: HttpServletRequest,
        response: HttpServletResponse,
        handler: Any
    ): Boolean {

        val uri = request.requestURI

        // ❗ НЕ ТРОГАЕМ POST, PUT, DELETE
        if (request.method != "GET") {
            return true
        }

        // Главная
        if (uri == "/") {
            response.sendRedirect("/ru/home")
            return false
        }

        if (excludedPaths.any { uri.startsWith(it) }) {
            return true
        }

        val segments = uri.split("/")
        val firstSegment = segments.getOrNull(1)

        if (firstSegment in supportedLangs) {
            if (segments.size == 2) {
                response.sendRedirect("/$firstSegment/home")
                return false
            }
            return true
        }

        response.sendRedirect("/ru$uri")
        return false
    }
}