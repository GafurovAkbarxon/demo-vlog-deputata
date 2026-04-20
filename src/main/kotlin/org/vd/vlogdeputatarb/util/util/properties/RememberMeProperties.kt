package org.vd.vlogdeputatarb.util.util.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration


@ConfigurationProperties(prefix = "spring.security.remember-me")
data class RememberMeProperties(
    val key: String
)