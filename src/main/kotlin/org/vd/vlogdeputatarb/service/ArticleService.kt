package org.vd.vlogdeputatarb.service

import jakarta.persistence.criteria.Predicate
import org.hibernate.Hibernate
import org.jsoup.Jsoup
import org.jsoup.safety.Safelist
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.vd.vlogdeputatarb.data.article.Block
import org.vd.vlogdeputatarb.controller.admin.article.CreateArticleRequest
import org.vd.vlogdeputatarb.controller.admin.article.CreateArticleTranslationRequest
import org.vd.vlogdeputatarb.controller.admin.article.UpdateArticleRequest
import org.vd.vlogdeputatarb.controller.web.article.ArticleAlternateLinkResponse
import org.vd.vlogdeputatarb.controller.web.article.ArticleViewResponse
import org.vd.vlogdeputatarb.data.article.Article
import org.vd.vlogdeputatarb.data.article.ArticleRepository
import org.vd.vlogdeputatarb.data.article.ArticleTranslation
import org.vd.vlogdeputatarb.data.article.ArticleTranslationRepository
import org.vd.vlogdeputatarb.data.comment.CommentRepository
import org.vd.vlogdeputatarb.data.tag.Tag
import org.vd.vlogdeputatarb.util.enums.ArticleSort
import org.vd.vlogdeputatarb.util.enums.BlockType
import org.vd.vlogdeputatarb.util.enums.Category
import org.vd.vlogdeputatarb.util.enums.Language
import org.vd.vlogdeputatarb.util.exception.NotFoundException
import org.vd.vlogdeputatarb.util.util.BlockMapper
import java.time.LocalDateTime

@Service
class ArticleService(
    private val articleRepository: ArticleRepository,
    private val articleTranslationRepository: ArticleTranslationRepository,
    private val fileService: FileService,
    private val blockMapper: BlockMapper,
    private val tagService: TagService,
    private val commentRepository: CommentRepository
) {

    @Transactional
    fun create(dto: CreateArticleRequest): ArticleViewResponse {
        val coverFile = dto.coverFile ?: throw Exception("Обложка пришла пустой")
        val category = dto.category ?: throw Exception("Категория пришла пустой")
        val language = dto.language ?: throw Exception("Язык пришел пустой")
        println("1:")
        println(dto.tagNames.size)
dto.tagNames.forEach { println(it) }
 println("2")
        val tags = tagService.resolveTags(dto.tagNames, language)
        println(tags.size)
tags.forEach { it.toString() }
        val coverFileName = fileService.saveImage(coverFile)

        // 4️⃣ сохраняем статью
        val article = Article(
            slug = dto.slug.trim(),
            category = category,
            coverName = coverFileName,
            coverPosition = dto.coverPosition.trim()
        )
        val savedArticle = articleRepository.save(article)
        val translation = ArticleTranslation(
            article = savedArticle,
            language = language,
            seoTitle = dto.title.trim(),
            seoDescription = dto.descriptions.trim(),
            title = dto.title.trim(),
            altCover = dto.altCover.trim(),
            blocksJson = cleanUnSafeBlocks(dto.blocksJson),
            tags = tags
        )
        val savedTranslation = articleTranslationRepository.save(translation)
        return toDto(savedTranslation)


    }

    fun existBySlug(slug: String): Boolean {
        return articleRepository.existsBySlug(slug)
    }

    private fun toDto(translation: ArticleTranslation): ArticleViewResponse {
        val article = translation.article
        val alternates = articleTranslationRepository
            .findAllByArticleId(article.id!!)
            .map {
                ArticleAlternateLinkResponse(
                    language = it.language,
                    slug = article.slug
                )
            }
        val alternateSlugs = alternates.associate { it.language.code to it.slug }
        return ArticleViewResponse(
            id = translation.id!!,
            language = translation.language,
            slug = article.slug,
            seoTitle = translation.seoTitle,
            seoDescription = translation.seoDescription,
            altCover = translation.altCover,
            title = translation.title,
            blocks = blockMapper.toBlock(translation.blocksJson),
            category = article.category,
            coverName = article.coverName,
            coverPosition = article.coverPosition,
            createdAt = article.createdAt,
            updatedAt = article.updatedAt,
            viewCount = article.viewCount,
            articleId = article.id!!,
            commentsEnabled = article.commentsEnabled,
            commentCount = article.commentCount,
            alternates = alternates,
            alternateSlugs = alternateSlugs,
            tags = translation.tags.map { it.name }
        )
    }

    private fun cleanUnSafeBlocks(jsonBlock: String): String {
        val blocks = blockMapper.toBlock(jsonBlock)
        if (blocks.isEmpty()) {
            throw Exception("Блоки статьи не может быть пустой")
        }
        val cleanedBlocks = blocks.map { cleanBlock(it) }
        return blockMapper.toJson(cleanedBlocks)
    }

    private fun cleanBlock(block: Block): Block {
        return when (block.type) {
            BlockType.TEXT,
            BlockType.QUOTE -> {
                Block(
                    type = block.type,
                    content = safeHtml(block.content ?: "")
                )
            }

            BlockType.IMAGE -> {
                // для image content = filename, НИЧЕГО не чистим
                Block(
                    type = block.type,
                    content = block.content
                )
            }

            else -> throw Exception("Неизвестный тип блока: ${block.type}")
        }
    }

    private fun safeHtml(input: String): String {
        return Jsoup.clean(
            input,
            Safelist.basic()
                .addTags(
                    "b", "strong", "i", "em", "u",
                    "a", "ul", "ol", "li", "p", "br",
                    "h1", "h2", "h3", "h4", "h5",
                    "span"
                )
                .addAttributes("a", "href")
                .addAttributes("span", "style")
        )
    }

    @Transactional(readOnly = true)
    fun getBySlugAndLanguage(
        slug: String,
        language: Language
    ): ArticleViewResponse {
        val article =
            articleRepository.findBySlug(slug) ?: throw NotFoundException("Article with slug: $slug not found")
        val translation = articleTranslationRepository.findByArticleIdAndLanguage(article.id!!, language)
            ?: throw NotFoundException("ArticleTranslation with articleId:${article.id!!} and language: $language not found")
        return toDto(translation)
    }

    @Transactional
    fun incrementViews(articleId: Long) {
        val updatedRows = articleRepository.incrementViewCount(articleId)
        if (updatedRows == 0) {
            throw NotFoundException("Статья с articleId=$articleId не найдена для incrementViews")
        }
    }
    fun getAll(): List<ArticleViewResponse>{
       return articleTranslationRepository.findAll().map { toDto(it) }
    }
    @Transactional(readOnly = true)
    fun filterArticles(
        language: Language,
        category: Category? = null,
        title: String? = null,
        tagName: String? = null,
        sort: ArticleSort? = null
    ): List<ArticleViewResponse> {
        val normalizedTagName = tagName?.trim()

        val tag = if (!normalizedTagName.isNullOrBlank()) {
            tagService.findAByNameAndLanguage(normalizedTagName, language)
                ?: return emptyList()
        } else {
            null
        }
        val spec = buildSpec(language, category, title, tag)

        val sortObj = when (sort) {
            ArticleSort.NEW ->
                Sort.by(Sort.Direction.DESC, "article.createdAt")

            ArticleSort.OLD ->
                Sort.by(Sort.Direction.ASC, "article.createdAt")

            ArticleSort.MOST_VIEWED ->
                Sort.by(Sort.Direction.DESC, "article.viewCount")

            ArticleSort.LEAST_VIEWED ->
                Sort.by(Sort.Direction.ASC, "article.viewCount")

            ArticleSort.MOST_COMMENTED ->
                Sort.by(Sort.Direction.DESC, "article.commentCount")

            ArticleSort.LEAST_COMMENTED ->
                Sort.by(Sort.Direction.ASC, "article.commentCount")

            null -> Sort.unsorted()
        }
        val articles = articleTranslationRepository.findAll(spec, sortObj)
        return articles.map { toDto(it) }
    }

    private fun buildSpec(
        language: Language,
        category: Category?,
        title: String?,
        tag: Tag?
    ): Specification<ArticleTranslation> {
        return Specification { root, query, cb ->
            val predicates = mutableListOf<Predicate>()

            predicates += cb.equal(root.get<Language>("language"), language)

            if (!title.isNullOrBlank()) {
                predicates += cb.like(
                    cb.lower(root.get("title")),
                    "%${title.trim().lowercase()}%"
                )
            }

            val articleJoin = root.join<ArticleTranslation, Article>("article")

            if (category != null) {
                predicates += cb.equal(articleJoin.get<Category>("category"), category)
            }

            if (tag != null) {
                predicates += cb.isMember(tag, root.get("tags"))
            }

            cb.and(*predicates.toTypedArray())
        }
    }

    @Transactional(readOnly = true)
    fun getById(id: Long): Article {
        return articleRepository.findById(id)
            .orElseThrow { NotFoundException("Article with articleId: $id not found") }
    }

    @Transactional
    fun createTranslation(dto: CreateArticleTranslationRequest): ArticleViewResponse {
        val articleId = dto.articleId
        val article = getById(articleId)

        if (articleTranslationRepository.existsByArticleIdAndLanguage(articleId, dto.language)) {
            throw Exception("Translation for ${dto.language} already exists")
        }


        val tags = tagService.resolveTags(dto.tagNames, dto.language)


        val translation = ArticleTranslation(
            article = article,
            language = dto.language,
            seoTitle = dto.title.trim(),
            seoDescription = dto.descriptions.trim(),
            title = dto.title.trim(),
            altCover = dto.altCover.trim(),
            blocksJson = cleanUnSafeBlocks(dto.blocksJson),
            tags = tags
        )


        val savedTranslation = articleTranslationRepository.save(translation)
        return toDto(savedTranslation)
    }

    @Transactional
    fun deleteById(id: Long) {
        val article = getById(id)
        fileService.deleteImage(article.coverName)
        commentRepository.deleteAllByArticleId(article.id!!)

        articleRepository.deleteById(id)
    }

    @Transactional(readOnly = true)
    fun getNewsForLast48Hours(): List<ArticleViewResponse> {
        val since = LocalDateTime.now().minusHours(48)
        return articleTranslationRepository
            .findNewsForSitemapSince(since)
            .map { toDto(it) }
    }
    @Transactional
    fun update(id: Long, dto: UpdateArticleRequest): ArticleViewResponse {

        val tags = tagService.resolveTags(dto.tagNames, dto.language)

        val articleTranslation = articleTranslationRepository.findById(id)
            .orElseThrow { NotFoundException("ArticleTranslation $id not found") }
        val article = articleTranslation.article

        article.category = dto.category
        article.coverPosition = dto.coverPosition
        article.commentsEnabled = dto.commentsEnabled
        article.updatedAt = LocalDateTime.now()
        dto.coverFile
            ?.takeIf { !it.isEmpty }
            ?.let { coverFile ->
                val coverName = fileService.saveImage(coverFile)
                fileService.deleteImage(article.coverName)
                article.coverName = coverName
            }
        article.slug = dto.slug.trim()
        articleTranslation.language = dto.language
        articleTranslation.seoDescription = dto.descriptions.trim()
        articleTranslation.seoTitle = dto.title.trim()
        articleTranslation.altCover = dto.altCover.trim()
        articleTranslation.title = dto.title.trim()
        articleTranslation.blocksJson = dto.blocksJson
        articleTranslation.tags.clear()
        articleTranslation.tags = tags

//  )
        val savedArticle = articleRepository.save(article)
        articleTranslation.article = savedArticle
        val savedTranslation = articleTranslationRepository.save(articleTranslation)
        return toDto(savedTranslation)
    }

    //cpmment
    fun updateCommentCount(articleId: Long, delta: Long) {
        val updatedRows = articleRepository.updateCommentCount(articleId, delta)
        if (updatedRows == 0) {
            throw NotFoundException("Статья с id=$articleId не найдена")
        }
    }

    //for favorite
    @Transactional(readOnly = true)
    fun getTranslationsByArticleIds(articleIds: List<Long>, language: Language): List<ArticleViewResponse> {
        if (articleIds.isEmpty()) return emptyList()
        val translations = articleTranslationRepository.findAllByArticleIdInAndLanguage(articleIds, language)
        return translations.map { toDto(it) }


    }
}