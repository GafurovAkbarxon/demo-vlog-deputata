package org.vd.vlogdeputatarb.data.favorite

import java.time.LocalDateTime

data class FavoriteArticleView(
    val articleId: Long,
    val title: String,
    val favoritedAt: LocalDateTime
)