package org.vd.vlogdeputatarb.controller.admin.article

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.web.multipart.MultipartFile
import org.vd.vlogdeputatarb.util.enums.Category
import org.vd.vlogdeputatarb.util.enums.Language
import java.io.Serializable

/**
 * DTO for {@link org.vd.vlogdeputatarb.models.Article}
 */
data class UpdateArticleRequest(

    @field:NotNull(message = "Язык обязателен")
    var language: Language,
    @field:NotEmpty(message = "Должен быть хотя бы один тег")
    var tagNames: List<String>,
    @field:NotBlank
    @field:Size(max = 255, message = "Заголовок слишком длинный")
    val title: String,
    @field:NotBlank(message = "Название для ссылки обязательна")
    @field:Size(max = 255, message = "Slug слишком длинный")
    @field:Pattern(
        regexp = "^[a-z0-9-]+$",
        message = "Slug может содержать только латиницу, цифры и дефис"
    )
    val slug: String,
    @field:NotBlank(message = "Мета описание обязательна")
    val descriptions: String,
    @field:NotBlank(message = "Alt обложки обязательна")
    val altCover: String,
    val coverFile: MultipartFile?,
    val coverPosition: String,
    @field:NotNull(message = "Категория обязательна")
    var category: Category,
    val commentsEnabled: Boolean =false,
    @field:NotBlank
    val blocksJson: String,
) : Serializable