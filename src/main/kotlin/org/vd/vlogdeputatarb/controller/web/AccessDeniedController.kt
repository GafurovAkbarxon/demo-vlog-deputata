package org.vd.vlogdeputatarb.controller.web

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping

@Controller
class AccessDeniedController {

    @GetMapping("/access-denied")
    fun accessDenied(model: Model): String {
        model.addAttribute("disableLangSwitch", true)
        return "access-denied"
    }
}