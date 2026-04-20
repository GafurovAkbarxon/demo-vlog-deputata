package org.vd.vlogdeputatarb

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.boot.runApplication
import org.springframework.scheduling.annotation.EnableScheduling

@EnableScheduling
@ConfigurationPropertiesScan
@SpringBootApplication
class VlogDeputataRbApplication

fun main(args: Array<String>) {

    runApplication<VlogDeputataRbApplication>(*args)
}
