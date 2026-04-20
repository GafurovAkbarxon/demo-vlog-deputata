package org.vd.vlogdeputatarb.controller.web.article

import org.vd.vlogdeputatarb.data.article.Article
import org.vd.vlogdeputatarb.data.article.ArticleTranslation
import org.vd.vlogdeputatarb.data.article.Block
import org.vd.vlogdeputatarb.data.tag.Tag
import org.vd.vlogdeputatarb.util.enums.Category
import org.vd.vlogdeputatarb.util.enums.Language
import java.time.LocalDateTime

data class ArticleViewResponse(

    val id: Long,
    var language: Language,
    var slug: String,
    var seoTitle: String,
    var seoDescription: String,
    var altCover: String,

    val title: String,
    val blocks: List<Block>,
    val tags: List<String>,
    val category: Category,
    var coverName:String,
    var coverPosition: String,
    val createdAt: LocalDateTime,
    var updatedAt: LocalDateTime,
    var viewCount: Long,
    val articleId: Long,
    var commentsEnabled: Boolean,
    var commentCount: Int,
    val alternates: List<ArticleAlternateLinkResponse>,
    val alternateSlugs: Map<String, String>
) {
}