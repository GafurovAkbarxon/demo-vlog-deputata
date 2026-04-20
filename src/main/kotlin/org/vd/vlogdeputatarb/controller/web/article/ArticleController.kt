package org.vd.vlogdeputatarb.controller.web.article

import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.vd.vlogdeputatarb.service.ArticleService
import org.vd.vlogdeputatarb.service.CommentService
import org.vd.vlogdeputatarb.service.FavoriteService
import org.vd.vlogdeputatarb.service.TagService
import org.vd.vlogdeputatarb.service.UserService
import org.vd.vlogdeputatarb.util.enums.ArticleSort
import org.vd.vlogdeputatarb.util.enums.Category
import org.vd.vlogdeputatarb.util.enums.Language
import org.vd.vlogdeputatarb.util.util.properties.AppProperties

@Controller
@RequestMapping("/{lang:ru|uz}/article")
class ArticleController(
    private val articleService: ArticleService,
    private val commentService: CommentService,
    private val tagService: TagService,
    private val app: AppProperties,
    private val userService: UserService,
    private val favoriteService: FavoriteService
) {


    @GetMapping("/{slug}")
    fun viewArticle(
        @PathVariable slug: String,
        @PathVariable lang: String,
        model: Model
    ): String {
        val language = Language.from(lang)

        val article = articleService.getBySlugAndLanguage(slug, language)

        articleService.incrementViews(article.articleId)

       val comments = commentService.getAllCommentsByArticleId(article.articleId)
        val filterArticles = articleService.filterArticles(Language.from(lang))

        val user = userService.getLoggedUserOrNull()
        val isFavorite = user?.let { favoriteService.isFavorite(it.id!!, article.articleId) } ?: false
        model.addAttribute("isFavorite", isFavorite)

        model.addAttribute("comments", comments)
        model.addAttribute("baseUrl", app.baseUrl)
        model.addAttribute("article", article)
        model.addAttribute("relatedArticles", filterArticles)
        model.addAttribute("disableLangSwitch", true);
        return "pages/web/article/view"
    }


    @GetMapping("/all")
    fun showArticles(
        model: Model,
        @PathVariable lang: String,
        @RequestParam("filterCategory") category: Category?,
        @RequestParam("filterTitle") title: String?,
        @RequestParam("filterTag") tagName: String?,
        @RequestParam("sort") sort: ArticleSort?
    ): String {
        val filterArticles =
            articleService.filterArticles(Language.from(lang), category, title, tagName, sort)
        val allTags = tagService.getAllByLanguage(Language.from(lang))
       model.addAttribute("categories", Category.entries.toTypedArray())
        model.addAttribute("allSorting", ArticleSort.entries.toTypedArray())
        model.addAttribute("allTags", allTags)
        model.addAttribute("articles", filterArticles)
        model.addAttribute("filterTitle", title)
        model.addAttribute("filterCategory", category)
        model.addAttribute("filterTag", tagName)
        return "pages/web/article/all"
    }


}