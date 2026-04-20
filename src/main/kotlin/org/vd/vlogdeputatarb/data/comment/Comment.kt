package org.vd.vlogdeputatarb.data.comment

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.ManyToOne
import org.vd.vlogdeputatarb.data.user.User
import org.vd.vlogdeputatarb.util.enums.BaseModel
import java.time.LocalDateTime

@Entity
class Comment(
    @Column(columnDefinition = "TEXT", nullable = false)
    val text: String,

    val createdAt: LocalDateTime = LocalDateTime.now(),

    val articleId: Long,

    /** NULL если гость */
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    var user: User


) : BaseModel()