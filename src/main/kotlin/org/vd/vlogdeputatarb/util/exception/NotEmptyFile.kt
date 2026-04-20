package org.vd.vlogdeputatarb.util.exception

import jakarta.validation.Constraint
import jakarta.validation.Payload
import kotlin.reflect.KClass

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
@Constraint(validatedBy = [NotEmptyFileValidator::class])
annotation class NotEmptyFile(
    val message: String = "Файл обязателен",
    val groups: Array<KClass<*>> = [],
    val payload: Array<KClass<out Payload>> = []
)