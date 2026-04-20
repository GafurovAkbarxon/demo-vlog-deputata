package org.vd.vlogdeputatarb.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.vd.vlogdeputatarb.controller.web.article.CommentDtoResponse
import org.vd.vlogdeputatarb.data.user.User
import org.vd.vlogdeputatarb.data.comment.Comment
import org.vd.vlogdeputatarb.data.comment.CommentRepository
import org.vd.vlogdeputatarb.util.exception.NotFoundException

@Service
class CommentService(
    private val commentRepository: CommentRepository,
    private val articleService: ArticleService
){

    @Transactional
    fun addComment(text: String, articleId: Long, user: User) {
        val article =articleService.getById(articleId)
        val comment = Comment(
            text = text,
            articleId = article.id!!,
            user = user
        )
        commentRepository.save(comment)
        articleService.updateCommentCount(articleId, 1)

    }

    @Transactional
    fun deleteComment(id: Long): Long {
        val comment = getById(id)
        commentRepository.delete(comment)
        articleService.updateCommentCount(comment.articleId, -1)
        return comment.articleId
    }



    fun getAllCommentsByArticleId(articleId: Long): List<CommentDtoResponse> {
        val article = articleService.getById(articleId)
        val comments=commentRepository.findByArticleIdOrderByCreatedAtDesc(article.id!!)
        return comments.map{
            CommentDtoResponse(
                id=it.id!!,
                text = it.text,
                createdAt = it.createdAt,
                articleId = it.articleId,
                userDisplayName = it.user.displayName!!,
                userAvatarFilename = it.user.avatarFilename
            )
        }
    }


    private fun getById(id: Long): Comment {
        return commentRepository.findById(id)
            .orElseThrow { NotFoundException("Comment $id not found") }
    }
}