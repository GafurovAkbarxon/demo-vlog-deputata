package org.vd.vlogdeputatarb.controller.account.profile.dto

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size
import org.springframework.web.multipart.MultipartFile

class UpdateProfileRequest {
    @NotBlank
    @field:Size(min = 3, max = 30, message = "Имя 3-30 символов")
    @field:Pattern(
        regexp = "^[a-zA-Zа-яА-Я0-9_ ]+$",
        message = "Только буквы, цифры и _"
    )
    var displayName: String? = null
    var avatarFile: MultipartFile? = null
}