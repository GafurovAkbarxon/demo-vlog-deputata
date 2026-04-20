package org.vd.vlogdeputatarb.controller.admin.article

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Size
import org.vd.vlogdeputatarb.util.enums.Language

data class CreateArticleTranslationRequest (
    @field:NotNull(message = "ArticleId не может быть пустым. Нет связки")
    var articleId: Long,
    @field:NotNull(message = "Язык обязателен")
    var language: Language,

    @field:NotEmpty(message = "Должен быть хотя бы один тег")
    var tagNames: List<String> =emptyList(),

    @field:Size(max = 255, message = "Заголовок слишком длинный")
    @field:NotBlank(message = "Заголовок обязателен")
        var title: String = "",
    @field:NotBlank(message = "Мета описание обязательна")
        var descriptions: String = "",
    @field:NotBlank(message = "Alt обложки обязательна")
        var altCover: String = "",
    @field:NotBlank(message = "Статья должна иметь структуру")
        var blocksJson: String = ""
    )