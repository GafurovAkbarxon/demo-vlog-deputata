package org.vd.vlogdeputatarb.controller.admin

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull
import jakarta.validation.constraints.PastOrPresent
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.web.multipart.MultipartFile
import org.vd.vlogdeputatarb.data.wiki.enums.City
import org.vd.vlogdeputatarb.data.wiki.enums.PersonType
import org.vd.vlogdeputatarb.data.wiki.enums.SportStatus
import org.vd.vlogdeputatarb.data.wiki.enums.SportType
import org.vd.vlogdeputatarb.util.exception.NotEmptyFile
import java.time.LocalDate

data class PersonRequest(



    @field:NotBlank(message = "Slug обязателен")
    @field:Pattern(
        regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
        message = "Slug: только латиница/цифры и дефисы (пример: shavkat-mirziyoyev)"
    )
    var slug: String = "",

    @field:NotBlank(message = "Имя RU обязательно")
    var ruName: String = "",

    @field:NotBlank(message = "Имя UZ обязательно")
    var uzName: String = "",
    @field:NotBlank(message = "Alt картинки обязательно")
    var alt: String = "",
    var file: MultipartFile?=null,

    @field:NotNull(message = "Дата рождения обязательна")
    @field:PastOrPresent(message = "Дата рождения не может быть в будущем")
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    var birthDate: LocalDate? = null,

    @field:PastOrPresent(message = "Дата смерти не может быть в будущем")
    @field:DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
    var deathDate: LocalDate? = null,

    @field:NotNull(message = "Место рождения обязательно")
    var birthPlace: City? = null,

    var ruDescription: String? = null,
    var uzDescription: String? = null,

    @field:NotNull(message = "Тип обязателен")
    var type: PersonType? = null,


    @field:Size(max = 500, message = "Ссылка слишком длинная")
    @field:Pattern(
        regexp = "^$|^https?://.+",
        message = "External URL должен начинаться с http:// или https://"
    )
    var externalUrl: String? = null,

    var sportType: SportType? = null,
    var sportStatus: SportStatus?=null)