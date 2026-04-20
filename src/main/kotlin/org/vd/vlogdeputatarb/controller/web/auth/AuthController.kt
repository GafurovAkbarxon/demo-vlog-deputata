package org.vd.vlogdeputatarb.controller.web.auth

import jakarta.servlet.http.HttpServletRequest
import jakarta.validation.Valid
import org.springframework.context.MessageSource
import org.springframework.context.i18n.LocaleContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.vd.vlogdeputatarb.service.UserService
import org.vd.vlogdeputatarb.service.CaptchaService
import org.vd.vlogdeputatarb.util.util.properties.TurnstileProperties

@RequestMapping("/auth")
@Controller
class AuthController(
    private val userService: UserService,
    private val captchaService: CaptchaService,
    private val turnstile: TurnstileProperties,
    private val messageSource: MessageSource
) {
    @GetMapping("/login")
    fun loginPage(model: Model,request: HttpServletRequest): String {
request.getSession(true)
        model.addAttribute("disableLangSwitch", true);
        return "pages/web/auth/login"
    }
    @GetMapping("/register") fun registerPage(model: Model): String {
        model.addAttribute("turnstileSiteKey", turnstile.siteKey)
        model.addAttribute("disableLangSwitch", true);
        return "pages/web/auth/register" }

    @PostMapping("/register")
    fun register(
        @Valid @ModelAttribute dto: SignUpRequest,
        binding: BindingResult,
        request: HttpServletRequest,
        model: Model
    ): String {
        val locale = LocaleContextHolder.getLocale()
        //honeybot
        if (!dto.website.isNullOrBlank()) {
            return "redirect:/auth/login"
        }
        val ip = request.remoteAddr

        val captchaRequired =
            captchaService.captchaRequiredForRegister(ip)

        if (captchaRequired) {

            if (dto.captchaToken.isNullOrBlank() ||
                !captchaService.verify(dto.captchaToken, "register",ip)
            ) {
                model.addAttribute("captchaRequired", true)
                model.addAttribute("error", messageSource.getMessage("captchaText", null, locale))
                model.addAttribute("disableLangSwitch", true);
                model.addAttribute("turnstileSiteKey", turnstile.siteKey)
                return "pages/web/auth/register"
            }
        }

        if (binding.hasErrors()) {
            model.addAttribute("error", binding.allErrors.first().defaultMessage)
            model.addAttribute("disableLangSwitch", true);

            return "pages/web/auth/register"
        }

        if (dto.password != dto.passwordConfirm) {
            model.addAttribute("error", messageSource.getMessage("passwordMismatch", null, locale))
            model.addAttribute("disableLangSwitch", true);

            return "pages/web/auth/register"
        }

        if (userService.existByUsername(dto.username)) {
            model.addAttribute("error", messageSource.getMessage("usernameNotFree", null, locale))
            model.addAttribute("disableLangSwitch", true);

            return "pages/web/auth/register"
        }

        userService.registerUser(dto.username, dto.password)

        return "redirect:/auth/login"
    }}