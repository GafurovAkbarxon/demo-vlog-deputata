package org.vd.vlogdeputatarb.data.favorite

import org.springframework.data.jpa.repository.JpaRepository

interface FavoriteArticleRepository : JpaRepository<FavoriteArticle, Long> {
    fun existsByUserIdAndArticleId(userId: Long, articleId: Long): Boolean
    fun findByUserId(userId: Long): List<FavoriteArticle>
    fun deleteByUserIdAndArticleId(userId: Long, articleId: Long): Long


}