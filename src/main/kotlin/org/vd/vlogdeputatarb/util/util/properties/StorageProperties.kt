package org.vd.vlogdeputatarb.util.util.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "app.storage")
data class StorageProperties(
    var root: String = "./storage"
)