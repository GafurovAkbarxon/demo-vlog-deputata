package org.vd.vlogdeputatarb.controller.admin.twoFa

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.core.session.SessionRegistry
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.vd.vlogdeputatarb.data.user.UserRepository
import org.vd.vlogdeputatarb.service.TotpService

@Controller
@RequestMapping("/auth/2fa")
class TwoFactorController(
) {
    @GetMapping
    fun twoFactorPage(model: Model): String {

        model.addAttribute("disableLangSwitch", true);
        return "pages/admin/2fa/code"
    }

}