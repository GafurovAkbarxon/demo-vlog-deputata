package org.vd.vlogdeputatarb.controller.admin.twoFa

import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.vd.vlogdeputatarb.data.user.UserRepository
import org.vd.vlogdeputatarb.service.TotpSecretCrypto
import org.vd.vlogdeputatarb.service.TotpService
import org.vd.vlogdeputatarb.service.UserService
import org.vd.vlogdeputatarb.util.enums.RoleType
import java.util.Base64

@PreAuthorize("hasRole('ADMIN')")
@Controller
@RequestMapping("/ru/admin/auth/2fa")
class AdminTwoFactorSetupController (
    private val userRepository: UserRepository,
    private val userService: UserService,
    private val totpService: TotpService,
    private val totpSecretCrypto: TotpSecretCrypto
){

    @GetMapping("/setup")
    fun setupPage(
        model: Model
    ): String {


        val user = userService.getLoggedUser()

        if (user.role != RoleType.ADMIN) {
            return "redirect:/ru"
        }
        if (user.twoFactorEnabled) {
            return "redirect:/ru"
        }

        //  создаем только если нет
        if (user.twoFactorSecret == null) {
            val credentials = totpService.createCredentials()
            val encrypted = totpSecretCrypto.encrypt(credentials.key)
            user.twoFactorSecret = encrypted
            userRepository.save(user)
        }

        //  используем существующий секрет
        val otpAuthUrl = totpService.buildOtpAuthUrl(
            user.username,
            totpSecretCrypto.decrypt(user.twoFactorSecret!!)
        )


        val qrImage = totpService.generateQrCodeImage(otpAuthUrl)
        val base64 = Base64.getEncoder().encodeToString(qrImage)

        model.addAttribute("qrImage", base64)
        model.addAttribute("disableLangSwitch", true);

        return "pages/admin/2fa/setup"
    }



    @PostMapping("/enable")
    fun enable2fa(
        @RequestParam code: String,
        model: Model
    ): String {

        val user = userService.getLoggedUser()

        val encryptedSecret = user.twoFactorSecret
            ?: return "redirect:/auth/login"

        if (!code.matches(Regex("\\d{6}"))) {
            return "redirect:/ru/admin/auth/2fa/setup?error"
        }
        val secret=totpSecretCrypto.decrypt(encryptedSecret)
        val valid = totpService.verifyCode(secret, code)
        if (!valid) {
            model.addAttribute("error", "Неверный код")
            model.addAttribute("disableLangSwitch", true);
            return "redirect:/ru/admin/auth/2fa/setup?error"
        }

        user.twoFactorEnabled = true
        userRepository.save(user)

        return "redirect:/ru/home"
    }

}