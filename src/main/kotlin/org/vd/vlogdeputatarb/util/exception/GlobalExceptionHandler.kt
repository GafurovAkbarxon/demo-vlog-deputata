package org.vd.vlogdeputatarb.util.exception

import jakarta.servlet.http.HttpServletRequest
import lombok.extern.slf4j.Slf4j
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
@Slf4j
class GlobalExceptionHandler {
private val log: Logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    // 404 не найдено не логируй
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException, model: Model): String {
        println("404 : ${ex.message}")
        model.addAttribute("status", 404)
        model.addAttribute("message",
             "Resource not found")
        return "error"
    }

    // 400 клиент виноват Bad Request не логируй
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: IllegalArgumentException, model: Model): String {
        println("400 : ${ex.message}")
        model.addAttribute("status", 400)
        model.addAttribute("message",  "Bad request")
        return "error"
    }

    // 500 архитектурная ошибка логируй
    @ExceptionHandler(Exception::class)
    fun handleServer(ex: Exception, model: Model,request: HttpServletRequest): String {
        log.error(
            "500 error: method={} uri={}",
            request.method,
            request.requestURI,
            ex
        )
        println("500 : ${ex.message}")
        ex.printStackTrace()
        model.addAttribute("status", 500)
        model.addAttribute("message",  "Internal server error")
        return "error"
    }

}