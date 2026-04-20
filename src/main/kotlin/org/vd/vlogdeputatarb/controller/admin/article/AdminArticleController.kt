package org.vd.vlogdeputatarb.controller.admin.article

import jakarta.validation.Valid
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.validation.BindingResult
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.mvc.support.RedirectAttributes
import org.vd.vlogdeputatarb.service.ArticleService
import org.vd.vlogdeputatarb.service.CommentService
import org.vd.vlogdeputatarb.service.TagService
import org.vd.vlogdeputatarb.util.enums.Category
import org.vd.vlogdeputatarb.util.enums.Language

@PreAuthorize("hasRole('ADMIN')")
@Controller
@RequestMapping("/ru/admin/article")
class AdminArticleController(
    private val articleService: ArticleService,
    private val tagService: TagService,

) {


    @GetMapping("/create")
    fun showCreateForm(model: Model): String {
        addEnums(model)
        model.addAttribute("dto", CreateArticleRequest())
        model.addAttribute("allTags", tagService.getAll())
        return "pages/admin/article/create"
    }

    @GetMapping("/{id}/create-translation")
    fun showCreateTranslation(
        @PathVariable id: Long,
        @RequestParam lang: String,
        model: Model
    ): String {
        val article = articleService.getById(id)
        addEnums(model)

        model.addAttribute("allTags", tagService.getAll())

        model.addAttribute("dto", CreateArticleTranslationRequest(article.id!!, Language.from(lang)));

        return "pages/admin/article/create-translation"
    }

    @PostMapping("/create")
    fun createArticle(
        @ModelAttribute ("dto") @Valid dto :CreateArticleRequest,
        bindingResult: BindingResult,
        model: Model,
        redirect: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            addEnums(model)
            model.addAttribute("allTags", tagService.getAll())

            return "pages/admin/article/create"
        }
        if (articleService.existBySlug(dto.slug)) {
            bindingResult.rejectValue("slug", "slug.duplicate", "Slug уже занят")
            addEnums(model)
            model.addAttribute("allTags", tagService.getAll())

            return "pages/admin/article/create"
        }
        val savedArticle =articleService.create(dto)

        val message = "Статья успешно создана"
        redirect.addFlashAttribute("is_done", true)
        redirect.addFlashAttribute("message", message)

        return "redirect:/${savedArticle.language.code}/article/${savedArticle.slug}"
    }

    @PostMapping("/create-translation")
    fun createArticleTranslation(
        @ModelAttribute ("dto") @Valid dto: CreateArticleTranslationRequest,
        bindingResult: BindingResult,
        model: Model,
        redirect: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            addEnums(model)
            model.addAttribute("allTags", tagService.getAll())

            return "pages/admin/article/create-translation"
        } else {
            val articleTranslation = articleService.createTranslation(dto)
            val message = "Статья успешно создана"
            redirect.addFlashAttribute("is_done", true)
            redirect.addFlashAttribute("message", message)

            return "redirect:/${articleTranslation.language.code}/article/${articleTranslation.slug}"
        }
    }

    @GetMapping("/edit/{slug}")
    fun getEditArticle(
        @PathVariable slug: String,
        @RequestParam contentLang: String, model: Model
    ): String {
        val article = articleService.getBySlugAndLanguage(slug, Language.from(contentLang))
addEnums(model)
        model.addAttribute("allTags", tagService.getAll())
        model.addAttribute("article", article)

        return "pages/admin/article/edit" // Thymeleaf-шаблон
    }

    @PostMapping("/edit/{id}")
    fun editArticle(
        @PathVariable id: Long,
        @ModelAttribute @Valid dto: UpdateArticleRequest,
        bindingResult: BindingResult,
        model: Model,
        redirect: RedirectAttributes
    ): String {
        if (bindingResult.hasErrors()) {
            addEnums(model)
            model.addAttribute("allTags", tagService.getAll())
            val message = "Ошибка.Форма не прошла серверную валидацию: ${bindingResult.allErrors}"
            model.addAttribute("is_done", false)
            model.addAttribute("message", message)
            model.addAttribute("disableLangSwitch", true);

            return "pages/admin/article/edit"
        } else {
            val translation =articleService.update(id, dto)
            val message = "Статья успешно изменена"
            redirect.addFlashAttribute("is_done", true)
            redirect.addFlashAttribute("message", message)

            return "redirect:/${translation.language.code}/article/${translation.slug}"
        }
    }

    @PostMapping("/delete/{id}")
    fun deleteArticle(
        @PathVariable id: Long
    ): String {
        articleService.deleteById(id)
        return "redirect:/ru/article/all"
    }
    private fun addEnums(model: Model) {
        model.addAttribute("languages", Language.entries.toTypedArray())
        model.addAttribute("categories", Category.entries.toTypedArray())
        model.addAttribute("disableLangSwitch", true);
    }
}