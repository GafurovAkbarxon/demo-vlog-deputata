package org.vd.vlogdeputatarb.util.exception

import jakarta.validation.ConstraintValidator
import jakarta.validation.ConstraintValidatorContext
import org.springframework.web.multipart.MultipartFile

class NotEmptyFileValidator : ConstraintValidator<NotEmptyFile, MultipartFile?> {
    override fun isValid(value: MultipartFile?, context: ConstraintValidatorContext): Boolean {
        return value != null && !value.isEmpty
    }
}