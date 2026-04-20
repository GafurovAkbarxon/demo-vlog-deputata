package org.vd.vlogdeputatarb.data.article

import jakarta.persistence.CascadeType
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.OneToMany
import jakarta.persistence.Table
import org.vd.vlogdeputatarb.data.tag.Tag
import org.vd.vlogdeputatarb.util.enums.BaseModel
import org.vd.vlogdeputatarb.util.enums.Category
import org.vd.vlogdeputatarb.util.enums.Language
import java.time.LocalDateTime

@Entity
@Table(name = "articles")
class Article(
    @Column(nullable = false, unique = true)
    var slug: String,
    @Column(nullable = false)
    var coverName:String,
    @Column(nullable = false)
    var coverPosition: String,
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    var category: Category,

    val createdAt: LocalDateTime = LocalDateTime.now(),
    var updatedAt: LocalDateTime = LocalDateTime.now(),
    var commentsEnabled: Boolean = true,

    /** Общие счётчики */
    var viewCount: Long = 0,
    var commentCount: Int = 0,

    ) : BaseModel()