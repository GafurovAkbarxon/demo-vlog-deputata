package org.vd.vlogdeputatarb.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.vd.vlogdeputatarb.data.article.ArticleRepository
import org.vd.vlogdeputatarb.data.favorite.FavoriteArticle
import org.vd.vlogdeputatarb.data.favorite.FavoriteArticleRepository
import org.vd.vlogdeputatarb.data.user.UserRepository

@Service
class FavoriteService(
    private val repo: FavoriteArticleRepository,
    private val userRepo: UserRepository,
    private val articleRepo: ArticleRepository
) {
    @Transactional
    fun add(userId: Long, articleId: Long) {
        if (repo.existsByUserIdAndArticleId(userId, articleId)) return
        val articleExist = articleRepo.existsById(articleId)
        if(articleExist)
        repo.save(FavoriteArticle(userId, articleId))
    }

    @Transactional
    fun remove(userId: Long, articleId: Long) {
        repo.deleteByUserIdAndArticleId(userId, articleId)
    }

    @Transactional(readOnly = true)
    fun isFavorite(userId: Long, articleId: Long): Boolean =
        repo.existsByUserIdAndArticleId(userId, articleId)

    @Transactional(readOnly = true)
    fun list(userId: Long): List<Long> {
        val alls = repo.findByUserId(userId)
        return if (!alls.isEmpty()) {
             alls.map { it.articleId }
        } else {
            emptyList()
        }
    }

}