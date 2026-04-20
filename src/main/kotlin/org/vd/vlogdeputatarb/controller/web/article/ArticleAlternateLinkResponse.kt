package org.vd.vlogdeputatarb.controller.web.article

import jakarta.persistence.Column
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne
import org.vd.vlogdeputatarb.data.user.User
import org.vd.vlogdeputatarb.util.enums.Language
import java.time.LocalDateTime

data class ArticleAlternateLinkResponse(
    val language: Language,
    val slug: String
)

data class CommentDtoResponse(
    val id:Long,
    val text: String,
    val createdAt: LocalDateTime,
    val articleId: Long,
    var userDisplayName: String,
    var userAvatarFilename: String?
)