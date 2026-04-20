package org.vd.vlogdeputatarb.util.config

import com.maxmind.db.CHMCache
import com.maxmind.geoip2.DatabaseReader
import org.springframework.context.MessageSource
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.support.ReloadableResourceBundleMessageSource
import org.springframework.core.io.ClassPathResource
import org.springframework.web.servlet.LocaleResolver
import org.springframework.web.servlet.i18n.CookieLocaleResolver
import java.io.File
import java.util.Locale

@Configuration
class I18nConfig {

    @Bean
    fun messageSource(): MessageSource {
        val source = ReloadableResourceBundleMessageSource()
        source.setBasename("classpath:messages")
        source.setDefaultEncoding("UTF-8")
        source.setFallbackToSystemLocale(false)
        return source
    }
    @Bean
    fun localeResolver(): LocaleResolver {
        val resolver = CookieLocaleResolver()
        resolver.setCookieName("lang")
        resolver.setDefaultLocale(Locale("ru"))
        return resolver
    }

    @Bean
    fun cityDatabaseReader(): DatabaseReader {
//        val file = File("src/main/resources/geo/GeoLite2-City.mmdb")
        val resource = ClassPathResource("geo/GeoLite2-City.mmdb")
//        return DatabaseReader.Builder(file)
//            .withCache(CHMCache())
//            .build()
        return DatabaseReader.Builder(resource.inputStream)
            .withCache(CHMCache())
            .build()
    }

    @Bean
    fun asnDatabaseReader(): DatabaseReader {
        val resource = ClassPathResource("geo/GeoLite2-ASN.mmdb")
        return DatabaseReader.Builder(resource.inputStream)
            .withCache(CHMCache())
            .build()
    }
}