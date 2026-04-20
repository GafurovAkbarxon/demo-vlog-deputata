package org.vd.vlogdeputatarb.data.article

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param

interface ArticleRepository : JpaRepository<Article, Long>, JpaSpecificationExecutor<Article> {

    //for create
    fun existsBySlug(slug: String): Boolean
    //for get
    fun findBySlug(slug: String): Article?
    //for increment view
    @Modifying
    @Query("UPDATE Article a SET a.viewCount = a.viewCount + 1 WHERE a.id = :id")
    fun incrementViewCount(@Param("id") id: Long): Int
    //for comment
    @Modifying
    @Query("""
    UPDATE Article a 
    SET a.commentCount = a.commentCount + :delta
    WHERE a.id = :articleId
""")
    fun updateCommentCount(
        @Param("articleId") articleId: Long,
        @Param("delta") delta: Long
    ): Int
}