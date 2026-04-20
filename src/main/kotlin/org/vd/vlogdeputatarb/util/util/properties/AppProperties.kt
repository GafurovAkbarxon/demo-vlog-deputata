package org.vd.vlogdeputatarb.util.util.properties

import org.springframework.boot.context.properties.ConfigurationProperties


@ConfigurationProperties(prefix = "site")
data class AppProperties(
     val hostname: String,
     val baseUrl:String
)