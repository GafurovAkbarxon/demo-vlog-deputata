package org.vd.vlogdeputatarb.controller.web.auth

import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Pattern
import jakarta.validation.constraints.Size

data class SignUpRequest(

    @field:NotBlank(message = "{usernameNotBlank}")
    @field:Size(min = 3, max = 30, message = "{usernameSize}")
    @field:Pattern(
        regexp = "^[A-Za-z0-9_]+$",
        message = "{usernamePattern}"
    )
    val username: String,

    @field:NotBlank(message = "{passwordNotBlank}")
    @field:Size(min = 6, max = 30, message = "{passwordSize}")
    val password: String,

    val passwordConfirm: String,

    val captchaToken: String?,
    var website: String? = null
)