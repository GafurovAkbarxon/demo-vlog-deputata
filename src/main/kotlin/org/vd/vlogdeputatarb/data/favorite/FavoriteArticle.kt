package org.vd.vlogdeputatarb.data.favorite

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.vd.vlogdeputatarb.data.article.Article
import org.vd.vlogdeputatarb.data.user.User
import org.vd.vlogdeputatarb.util.enums.BaseModel
import java.time.LocalDateTime

@Entity
@Table(
    name = "user_favorite_articles",
    uniqueConstraints = [UniqueConstraint(columnNames = ["user_id", "article_id"])],
    indexes = [
        Index(name = "idx_fav_user", columnList = "user_id"),
        Index(name = "idx_fav_article", columnList = "article_id")
    ]
)
class FavoriteArticle(
    @Column(nullable = false)
    val userId: Long,
    @Column(nullable = false)
    val articleId: Long,
    val createdAt: LocalDateTime = LocalDateTime.now()
) : BaseModel()