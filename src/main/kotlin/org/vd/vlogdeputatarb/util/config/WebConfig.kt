package org.vd.vlogdeputatarb.util.config

import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer
import org.vd.vlogdeputatarb.util.util.properties.StorageProperties
import java.nio.file.Files
import java.nio.file.Paths

@Configuration
class WebConfig(
    private val languageRedirectInterceptor: LanguageRedirectInterceptor,
    private val localeFromPathInterceptor: LocaleFromPathInterceptor,
    private val storageProperties: StorageProperties
) : WebMvcConfigurer {
    override fun addResourceHandlers(registry: ResourceHandlerRegistry) {

    val storageRoot = Paths.get(storageProperties.root).toAbsolutePath().normalize()
    val uploadsRoot = storageRoot.resolve("uploads")
    val imagesRoot = uploadsRoot.resolve("images")
    val videosRoot = uploadsRoot.resolve("videos")

    Files.createDirectories(imagesRoot)
    Files.createDirectories(videosRoot)

        
    registry.addResourceHandler("/uploads/images/**")
        .addResourceLocations(imagesRoot.toUri().toString())
        //                .addResourceLocations("file:///C:/vd/upload/images/")

    registry.addResourceHandler("/uploads/videos/**")
        .addResourceLocations(videosRoot.toUri().toString())
        //                .addResourceLocations("file:///C:/vd/upload/videos/")
}


    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(languageRedirectInterceptor)
            .addPathPatterns("/**")

        registry.addInterceptor(localeFromPathInterceptor)
            .addPathPatterns("/**")
    }
}