package org.vd.vlogdeputatarb.controller.web

import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.vd.vlogdeputatarb.util.enums.ArticleSort
import org.vd.vlogdeputatarb.util.enums.Category
import org.vd.vlogdeputatarb.util.enums.Language
import org.vd.vlogdeputatarb.service.ArticleService
import org.vd.vlogdeputatarb.service.TagService

@Controller
@RequestMapping("/{lang:ru|uz}")
class MainController(
    private val articleService: ArticleService,
    private val tagService: TagService
) {

    @GetMapping("","/home")
    fun getHome(
        model: Model,
        @PathVariable lang: String
    ): String {
        val filterArticles = articleService.filterArticles(language = Language.from(lang))
        model.addAttribute("articles", filterArticles)
        return "pages/web/home"
    }



    @GetMapping("/uzbekistan")
    fun getUzbekistan(
        model: Model,
        @PathVariable lang: String
    ): String {
        val filterArticles = articleService.filterArticles(language = Language.from(lang))

        model.addAttribute("articles",filterArticles)
        return "pages/web/uzbekistan"
    }

    @GetMapping("/news")
    fun getNews(
        model: Model,
        @PathVariable lang: String
    ): String {
        val filterArticles = articleService.filterArticles(Language.from(lang), Category.NEWS)
        val allTags = tagService.getAllByLanguage(Language.from(lang))

        model.addAttribute("filterCategory", Category.NEWS)
        model.addAttribute("articles",filterArticles)
        model.addAttribute("categories", Category.entries.toTypedArray())
        model.addAttribute("allSorting", ArticleSort.entries.toTypedArray())
        model.addAttribute("allTags",allTags )

        return "pages/web/article/all"
    }

    @GetMapping("/articles")
    fun getArticles(
        model: Model,
        @PathVariable lang: String
    ): String {
        val filterArticles = articleService.filterArticles(Language.from(lang), Category.ARTICLES)
        val allTags = tagService.getAllByLanguage(Language.from(lang))

        model.addAttribute("filterCategory", Category.ARTICLES)
        model.addAttribute("articles",filterArticles)
        model.addAttribute("categories", Category.entries.toTypedArray())
        model.addAttribute("allSorting", ArticleSort.entries.toTypedArray())
        model.addAttribute("allTags",allTags )
        return "pages/web/article/all"
    }
    @GetMapping("/problems")
    fun getProblems(
        model: Model,
        @PathVariable lang: String
    ): String {
        val filterArticles = articleService.filterArticles(Language.from(lang), Category.PROBLEMS)
        val allTags = tagService.getAllByLanguage(Language.from(lang))

        model.addAttribute("filterCategory", Category.PROBLEMS)
        model.addAttribute("articles",filterArticles)
        model.addAttribute("categories", Category.entries.toTypedArray())
        model.addAttribute("allSorting", ArticleSort.entries.toTypedArray())
        model.addAttribute("allTags",allTags )

        return "pages/web/article/all"
    }
}