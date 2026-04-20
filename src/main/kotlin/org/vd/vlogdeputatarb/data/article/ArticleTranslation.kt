package org.vd.vlogdeputatarb.data.article

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.JoinColumn
import jakarta.persistence.JoinTable
import jakarta.persistence.ManyToMany
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint
import org.vd.vlogdeputatarb.data.tag.Tag
import org.vd.vlogdeputatarb.util.enums.BaseModel
import org.vd.vlogdeputatarb.util.enums.Language

@Entity
@Table(
    name = "article_translations",
    uniqueConstraints = [
        UniqueConstraint(columnNames = ["article_id", "language"]),
    ]
)
class ArticleTranslation(

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "article_id", nullable = false)
     var article: Article,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var language: Language,

    @Column(nullable = false)
    var title: String,


    @Column(nullable = false,columnDefinition = "TEXT")
    var blocksJson: String,

    @Column(nullable = false)
    var seoTitle: String,
    @Column(nullable = false)
    var seoDescription: String,
    @Column(nullable = false)
    var altCover: String,

    @ManyToMany
    @JoinTable(
        name = "article_tag",
        joinColumns = [JoinColumn(name = "article_id")],
        inverseJoinColumns = [JoinColumn(name = "tag_id")]
    )
    var tags: MutableSet<Tag> = mutableSetOf()
    ) : BaseModel()