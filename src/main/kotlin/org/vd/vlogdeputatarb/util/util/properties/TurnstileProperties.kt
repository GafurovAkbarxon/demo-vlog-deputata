package org.vd.vlogdeputatarb.util.util.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "turnstile")
data class TurnstileProperties(
    val siteKey: String,
    val secret: String
)