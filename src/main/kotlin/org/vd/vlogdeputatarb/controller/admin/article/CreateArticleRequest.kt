package org.vd.vlogdeputatarb.controller.admin.article

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotEmpty
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.web.multipart.MultipartFile
import org.vd.vlogdeputatarb.util.enums.Category
import org.vd.vlogdeputatarb.util.enums.Language
import org.vd.vlogdeputatarb.util.exception.NotEmptyFile
import java.io.Serializable

data class CreateArticleRequest(
    @field:NotBlank(message = "Название для ссылки обязательна")
    @field:Size(max = 255, message = "Slug слишком длинный")
    @field:Pattern(
        regexp = "^[a-z0-9-]+$",
        message = "Slug может содержать только латиницу, цифры и дефис"
    )
    var slug: String = "",
    @field:NotEmptyFile(message = "Обложка обязательна")
    var coverFile: MultipartFile?=null,
    @field:NotBlank(message = "Позиция обложки обязательна")
    var coverPosition: String = "50% 50%",
    @field:NotNull(message = "Категория обязательна")
    var category: Category? = null,




    @field:NotNull(message = "Язык обязателен")
    var language: Language? = null,
    @field:NotBlank(message = "Заголовок обязателен")
    @field:Size(max = 255, message = "Заголовок слишком длинный")
    var title: String = "",
    @field:NotBlank(message = "Статья должна иметь структуру")
    var blocksJson: String = "",
    @field:NotBlank(message = "Мета описание обязательна")
    var descriptions: String = "",
    @field:NotBlank(message = "Alt обложки обязательна")
    var altCover: String = "",



    @field:NotEmpty(message = "Должен быть хотя бы один тег")
    var tagNames: List<String> =emptyList(),






) : Serializable