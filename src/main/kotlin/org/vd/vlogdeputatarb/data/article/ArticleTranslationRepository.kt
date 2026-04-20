package org.vd.vlogdeputatarb.data.article

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.vd.vlogdeputatarb.util.enums.Language
import java.time.LocalDateTime

interface ArticleTranslationRepository : JpaRepository<ArticleTranslation, Long>,
    JpaSpecificationExecutor<ArticleTranslation> {
   //for toDto(alternate)
   fun findAllByArticleId(articleId: Long): List<ArticleTranslation>

   //for get
   @Query("""
    select distinct at
    from ArticleTranslation at
    left join fetch at.tags
    where at.article.id = :articleId
      and at.language = :lang
""")
   fun findByArticleIdAndLanguage(articleId: Long, lang: Language): ArticleTranslation?

   //for createTranslation
   fun existsByArticleIdAndLanguage(articleId: Long, language: Language): Boolean


   //for favoriteService
   fun findAllByArticleIdInAndLanguage(articleIds: List<Long>, language: Language): List<ArticleTranslation>
//for Sitemap
   @Query("""
    select distinct at
    from ArticleTranslation at
    join fetch at.article a
    left join fetch at.tags t
    where a.createdAt >= :since
    order by a.createdAt desc
""")
   fun findNewsForSitemapSince(
      @Param("since") since: LocalDateTime
   ): List<ArticleTranslation>






}