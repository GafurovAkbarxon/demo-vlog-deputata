package org.vd.vlogdeputatarb.controller.account

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.vd.vlogdeputatarb.service.UserService
import org.vd.vlogdeputatarb.service.CommentService

@PreAuthorize("isAuthenticated()")
@Controller
@RequestMapping("/comments")
class CommentController (
    private val commentService: CommentService,
    private val userService: UserService

){
    @PostMapping("/add")

    fun addComment(
        @RequestParam text: String,
        @RequestParam articleId: Long,
        request: HttpServletRequest
    ): String {

        val referer = request.getHeader("Referer") ?: "/"

        if (text.isBlank()) return "redirect:$referer"

        val user = try {
            userService.getLoggedUser()
        } catch (e: IllegalStateException) {
            return "redirect:/login"
        }

        commentService.addComment(text, articleId, user)

        return "redirect:$referer"
    }




}