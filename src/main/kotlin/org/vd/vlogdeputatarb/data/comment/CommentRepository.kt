package org.vd.vlogdeputatarb.data.comment

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.transaction.annotation.Transactional

interface CommentRepository : JpaRepository<Comment, Long> {
    fun findByArticleIdOrderByCreatedAtDesc(articleId: Long): List<Comment>
    @Modifying
    @Query("delete from Comment c where c.articleId = :articleId")
    fun deleteAllByArticleId(@Param("articleId") articleId: Long)

    @Modifying
    @Transactional
    @Query("delete from Comment c where c.user.id = :userId")
    fun deleteByUserId(@Param("userId") userId: Long): Int
}