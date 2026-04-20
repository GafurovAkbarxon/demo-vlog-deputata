package org.vd.vlogdeputatarb.controller.account

import jakarta.servlet.http.HttpServletRequest
import org.springframework.data.domain.Pageable
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.vd.vlogdeputatarb.data.article.ArticleTranslation
import org.vd.vlogdeputatarb.service.ArticleService
import org.vd.vlogdeputatarb.service.FavoriteService
import org.vd.vlogdeputatarb.service.UserService
import org.vd.vlogdeputatarb.util.enums.Language

@PreAuthorize("isAuthenticated()")
@Controller("")
@RequestMapping("/{lang:ru|uz}/profile/favorite")
class FavoriteController(
    private val favoriteService: FavoriteService,
    private val userService: UserService,
    private val articleService: ArticleService
) {

    @PostMapping("/add")

    fun add(
        @RequestParam articleId: Long,
        @RequestParam slug:String,
        @PathVariable lang: String,
    ): String {

        val user = userService.getLoggedUser()
        favoriteService.add(user.id!!, articleId)
        return "redirect:/$lang/article/$slug"
    }

    @PostMapping("/delete")
    fun remove(
        @RequestParam slug:String,
        @RequestParam articleId: Long,
        @PathVariable lang: String
    ): String {

        val user = userService.getLoggedUser()
        favoriteService.remove(user.id!!,articleId)
        return "redirect:/$lang/article/$slug"
    }



    @GetMapping("/all")
    fun favorites(model: Model, @PathVariable lang: String): String {
        val user = userService.getLoggedUser()

        val language = when (lang) {
            "ru" -> Language.RU
            "uz" -> Language.UZ
            else -> Language.RU
        }

        val savedArticleIds = favoriteService.list(user.id!!)
        val translations = articleService.getTranslationsByArticleIds(savedArticleIds, language)

        model.addAttribute("all", translations)
        return "pages/account/favorite"
    }
    private fun safeRedirect(redirect: String?): String? =
        redirect?.takeIf { it.startsWith("/") && !it.startsWith("//") }
}