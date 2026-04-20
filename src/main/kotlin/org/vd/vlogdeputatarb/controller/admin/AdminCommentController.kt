package org.vd.vlogdeputatarb.controller.admin

import jakarta.servlet.http.HttpServletRequest
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.vd.vlogdeputatarb.service.CommentService

@PreAuthorize("hasRole('ADMIN')")
@Controller
@RequestMapping("/ru/admin/comments")
class AdminCommentController(
    private val commentService: CommentService,
) {

    @PostMapping("/delete/{id}")

    fun deleteComment(
        @PathVariable id: Long,
        request: HttpServletRequest
    ): String {

        commentService.deleteComment(id)

        val referer = request.getHeader("Referer")
            ?.takeIf { it.contains("/article/") }
            ?: "/"

        return "redirect:$referer"
    }
}