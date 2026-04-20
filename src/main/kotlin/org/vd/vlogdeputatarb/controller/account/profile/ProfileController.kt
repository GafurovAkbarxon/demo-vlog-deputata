package org.vd.vlogdeputatarb.controller.account.profile

import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.vd.vlogdeputatarb.controller.account.profile.dto.ChangePasswordRequest
import org.vd.vlogdeputatarb.controller.account.profile.dto.UpdateProfileRequest
import org.vd.vlogdeputatarb.service.SessionService
import org.vd.vlogdeputatarb.service.UserService

@PreAuthorize("isAuthenticated()")
@Controller
@RequestMapping("/{lang:ru|uz}/profile")
class ProfileController (
    private val  userService: UserService,
    private val  sessionService: SessionService,
){

    @GetMapping
    fun getProfile(
        model: Model,
        request: HttpServletRequest,
    ): String {
        val loggedUser = userService.getLoggedUser()

        val sessions = sessionService.getUserSessions(loggedUser,request.session.id)

        model.addAttribute("sessions", sessions)
        model.addAttribute("currentSessionId", request.session.id)

        model.addAttribute("user",loggedUser)
        return "pages/account/profile"
    }

    @PostMapping
    fun updateProfile(
        dto: UpdateProfileRequest,
        @PathVariable lang:String
    ): String {
        val user = userService.getLoggedUser()
        userService.updateProfile(user.id!!, dto)

        return "redirect:/{lang}/profile"
    }

    @PostMapping("/change-password")
    fun changePassword(
        @RequestBody dto: ChangePasswordRequest,
        request: HttpServletRequest
    ): ResponseEntity<String> {
        val logged=userService.getLoggedUser()
        userService.changePassword(dto)
        sessionService.expireAllExcept(logged, request.session.id)
        return ResponseEntity.ok("Password changed")//what
    }




    @PostMapping("/expire/{sessionId}")
    fun expireSession(@PathVariable sessionId: String,@PathVariable lang: String): String {
        val user = userService.getLoggedUser()

        val session = sessionService.getUserSessions(user, "")
            .find { it.sessionId == sessionId }
            ?: return "redirect:/$lang/profile"

        sessionService.expire(sessionId)
        return "redirect:/$lang/profile"
    }

    @PostMapping("/expire-others")
    fun expireOthers(request: HttpServletRequest,@PathVariable lang: String): String {
        val loggedUser = userService.getLoggedUser()
        sessionService.expireAllExcept(loggedUser, request.session.id)
        return "redirect:/$lang/profile"
    }
}