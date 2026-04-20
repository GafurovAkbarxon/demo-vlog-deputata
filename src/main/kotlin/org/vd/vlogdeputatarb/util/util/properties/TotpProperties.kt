package org.vd.vlogdeputatarb.util.util.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.totp")
data class TotpProperties (
    val encKey: String
)