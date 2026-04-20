package org.vd.vlogdeputatarb.controller.admin.user

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.vd.vlogdeputatarb.service.AdminUserService
import org.vd.vlogdeputatarb.service.LoginAttemptService
import org.vd.vlogdeputatarb.service.LoginEventService
import org.vd.vlogdeputatarb.service.SessionService
import org.vd.vlogdeputatarb.util.enums.AuthProvider
import org.vd.vlogdeputatarb.util.enums.RoleType

@Controller
@RequestMapping("/ru/admin/users")
@PreAuthorize("hasRole('ADMIN')")
class AdminUserController(
    private val adminUserService: AdminUserService,
    private val loginEventService: LoginEventService,
    private val loginAttemptService: LoginAttemptService,
    private val sessionService: SessionService
) {

    @GetMapping
    fun users(
        model: Model,
        filter: AdminUserFilterRequest
    ):String {
        val page = adminUserService.getUsers(
            filter.toSpec(),
            filter.toPageable()
        )
        model.addAttribute("roles", RoleType.entries.toTypedArray())
        model.addAttribute("providers", AuthProvider.entries.toTypedArray())
        model.addAttribute("users", page)
        model.addAttribute("filter", filter)
        model.addAttribute("disableLangSwitch", true);

        return "pages/admin/user/users"
    }

    @PostMapping("/{id}/block")
    fun block(
        @PathVariable id: Long
    ): String {
        adminUserService.blockUser(id)
        return "redirect:/ru/admin/users"
    }

    @PostMapping("/{id}/unblock")
    fun unblock(
        @PathVariable id: Long
    ): String {
        adminUserService.unblockUser(id)
        return "redirect:/ru/admin/users"
    }

    @PostMapping("/{id}/delete")
    fun delete(
        @PathVariable id: Long
    ): String {
        adminUserService.deleteUser(id)
        return "redirect:/ru/admin/users"
    }

    @GetMapping("/{id}")
    fun userDetails(
        @PathVariable id: Long,
        model: Model
    ):String  {
        val us=adminUserService.getUserDetails(id)
        model.addAttribute("user",us )
        model.addAttribute("userSessions",us.sessions )
        model.addAttribute("userComments",us.comments )

        model.addAttribute("disableLangSwitch", true)


        return "pages/admin/user/userInfo"
    }

    @PostMapping("/{userId}/sessions/{sessionId}/expire")
    fun expireUserSession(
        @PathVariable userId: Long,
        @PathVariable sessionId: String
    ): String {
        // Проверяем что юзер существует
        val user = adminUserService.getUserDetails(userId)


        // Завершаем сессию
        sessionService.expire(sessionId)

        return "redirect:/ru/admin/users/$userId"
    }




    @GetMapping("/login-events")
    fun events(
        filter: AdminLoginEventFilterRequest,
        model: Model,
        request: HttpServletRequest
    ): String {

        val page = loginEventService.getEvents(
            filter.toSpec(),
            filter.toPageable()
        )
//        sql optimized
        val blockedIps =page.content
            .map { it.ip }
            .filter { loginAttemptService.isBlocked(it,"__GLOBAL__") }
            .toList()
        model.addAttribute("blockedIps", blockedIps)
        model.addAttribute("page", page)
        model.addAttribute("filter", filter)
        model.addAttribute("browsers", loginEventService.browsers())
        model.addAttribute("oses", loginEventService.osList())
        model.addAttribute("devices", loginEventService.devices())

        model.addAttribute("suspiciousIps", loginEventService.suspiciousIps().toList())
        model.addAttribute("stats", loginEventService.stats())
        model.addAttribute("currentUri", request.requestURI)
        model.addAttribute("currentParams", request.parameterMap)
        model.addAttribute("disableLangSwitch", true);

        return "pages/admin/user/login-events"
    }



    @PostMapping("/block-ip")
    fun blockIp(@RequestParam ip: String): String {
        loginAttemptService.blockIpManually(ip)
        return "redirect:/ru/admin/users/login-events"
    }

    @PostMapping("/unblock-ip")
    fun unblockIp(@RequestParam ip: String): String {
        loginAttemptService.unblockIp(ip)
        return "redirect:/ru/admin/users/login-events"
    }
}