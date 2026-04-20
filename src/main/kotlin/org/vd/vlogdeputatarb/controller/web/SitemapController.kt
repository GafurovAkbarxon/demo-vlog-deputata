package org.vd.vlogdeputatarb.controller.web

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ResponseBody
import org.vd.vlogdeputatarb.data.article.ArticleTranslationRepository
import org.vd.vlogdeputatarb.service.ArticleService
import org.vd.vlogdeputatarb.service.TagService
import org.vd.vlogdeputatarb.util.util.properties.AppProperties
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Controller
class SitemapController(
    private val articleService: ArticleService,
    private val app: AppProperties,
) {

    private val dateFormatter = DateTimeFormatter.ISO_DATE
    private val dateTimeFormatter = DateTimeFormatter.ISO_DATE_TIME

    // -----------------------------
    // Обычный sitemap.xml
    // -----------------------------
    @GetMapping(
        value = ["/sitemap.xml"],
        produces = ["application/xml"]
    )
    @ResponseBody
    fun sitemap(): String {

        val translations = articleService.getAll()

        val urls = translations.joinToString("\n") { t ->
            """
           <url>
              <loc>${app.baseUrl}/${t.language.code}/article/${t.slug}</loc>
              <lastmod>${t.updatedAt.format(dateFormatter)}</lastmod>
              <image:image>
                <image:loc>${app.baseUrl}/uploads/images/${t.coverName}</image:loc>
                <image:title>${'$'}{escapeXml(t.title)}</image:title>              
               </image:image>
           </url>
            """.trimIndent()
        }

        return """
          <?xml version="1.0" encoding="UTF-8"?>
          <urlset
             xmlns="http://www.sitemaps.org/schemas/sitemap/0.9"
             xmlns:image="http://www.google.com/schemas/sitemap-image/1.1">
             $urls
          </urlset>
""".trimIndent()
    }

    // -----------------------------
    // Google News sitemap
    // -----------------------------
    @GetMapping(
        value = ["/news-sitemap.xml"],
        produces = ["application/xml"]
    )
    @ResponseBody
    fun newsSitemap(): String {


        val news = articleService.getNewsForLast48Hours()

        val urls = news.joinToString("\n") { t ->
                val keywords = escapeXml(
                    t.tags
                        .joinToString(", ") { it }
                )
            """
            <url>
                <loc>${app.baseUrl}/${t.language.code}/article/${t.slug}</loc>
                <news:news>
                    <news:publication>
                        <news:name>Vlog Deputata</news:name>
                        <news:language>${t.language.code}</news:language>
                    </news:publication>
                    <news:publication_date>
                        ${t.createdAt.format(dateTimeFormatter)}
                    </news:publication_date>
                    <news:title>${escapeXml(t.title)}</news:title>
                    <news:genres>News, Opinion, Blog</news:genres>
                    <news:keywords>$keywords</news:keywords>                
                </news:news>
            </url>
            """.trimIndent()
        }

        return """
        <?xml version="1.0" encoding="UTF-8"?>
        <urlset
            xmlns="http://www.sitemaps.org/schemas/sitemap/0.9"
            xmlns:news="http://www.google.com/schemas/sitemap-news/0.9">
            $urls
        </urlset>
        """.trimIndent()
    }

    // -----------------------------
    // XML escaping
    // -----------------------------
    private fun escapeXml(text: String): String =
        text
            .replace("&", "&amp;")
            .replace("<", "&lt;")
            .replace(">", "&gt;")
            .replace("\"", "&quot;")
            .replace("'", "&apos;")
}