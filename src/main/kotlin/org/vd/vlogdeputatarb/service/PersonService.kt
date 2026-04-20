package org.vd.vlogdeputatarb.service

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.vd.vlogdeputatarb.controller.admin.PersonRequest
import org.vd.vlogdeputatarb.data.wiki.Person
import org.vd.vlogdeputatarb.data.wiki.PersonRepository
import org.vd.vlogdeputatarb.controller.web.persons.PersonSpecifications
import org.vd.vlogdeputatarb.controller.web.persons.dto.PersonFilterRequest
import org.vd.vlogdeputatarb.controller.web.persons.dto.PersonResponse
import org.vd.vlogdeputatarb.util.enums.Language
import java.time.LocalDate

@Service
class PersonService(
    private val repo: PersonRepository,
    private val fileService: FileService
) {
    @Transactional
    fun exist(slug: String): Boolean {
        return repo.existsBySlug(slug.trim())
    }

    @Transactional
    fun slugTakenForOther(slug: String, id: Long): Boolean =
        repo.existsBySlugAndIdNot(slug.trim(), id)


    @Transactional(readOnly = true)
    fun getEntity(id: Long): Person =
        repo.findById(id).orElseThrow { IllegalArgumentException("Person not found: $id") }

    @Transactional
    fun create(p: PersonRequest): Person {
        p.slug = p.slug.trim()
        if (repo.existsBySlug(p.slug)) {
            throw IllegalArgumentException("Slug already exists: ${p.slug}")
        }
        val file = p.file ?: throw IllegalArgumentException("Картинка обязательна")
        val fileName = fileService.saveImage(file)
        val person = Person(
            slug = p.slug,
            ruName = p.ruName,
            uzName = p.uzName,
            imageFilename = fileName,
            birthDate = p.birthDate!!,
            deathDate = p.deathDate,
            birthPlace = p.birthPlace!!,
            type = p.type!!,
            ruDescription = p.ruDescription ,
            uzDescription = p.uzDescription,
            externalUrl = p.externalUrl,
            sportType = p.sportType,
            sportStatus = p.sportStatus,
            alt = p.alt
        )
        return repo.save(person)
    }



    @Transactional
    fun update(id: Long, dto: PersonRequest): Person {
        val p = repo.findById(id).orElseThrow { IllegalArgumentException("Person not found: $id") }
        p.slug = dto.slug
        p.ruName = dto.ruName
        p.uzName = dto.uzName
        dto.file?.takeIf { !it.isEmpty }?.let {
            p.imageFilename = fileService.saveImage(it)
        }
        p.birthDate = dto.birthDate!!
        p.deathDate = dto.deathDate
        p.birthPlace = dto.birthPlace!!
        p.type = dto.type!!
        p.ruDescription = dto.ruDescription
        p.uzDescription = dto.uzDescription
        p.externalUrl = dto.externalUrl
        p.sportType = dto.sportType
        p.sportStatus = dto.sportStatus
        p.alt = dto.alt

        return repo.save(p)

    }

    @Transactional
    fun delete(id: Long) {
        repo.deleteById(id)
    }


    @Transactional(readOnly = true)
    fun search(filter: PersonFilterRequest, lang: Language): List<PersonResponse> {
        val now = LocalDate.now()
        val spec = PersonSpecifications.byFilter(filter, now)

        val persons = repo.findAll(spec, Sort.by(Sort.Direction.ASC, "ruName"))

        return persons.map { p ->
            val isAlive = p.deathDate == null
            val age = if (isAlive) p.ageYears(now) else (p.deathAgeYears ?: p.ageYears(now))
            PersonResponse(
                id = p.id!!,
                slug = p.slug,
                name = p.name(lang),
                birthPlace = p.birthPlace,
                birthDate = p.birthDate,
                zodiac = p.zodiac,
                isAlive = isAlive,
                ageYears = age,
                deathDate = p.deathDate,
                deathAgeYears = p.deathAgeYears,
                type = p.type,
                sportType = p.sportType,
                sportStatus = p.sportStatus,
                imageFilename = p.imageFilename,
                alt = p.alt,
                externalUrl = p.externalUrl,
                description = p.description(lang)
            )
        }
    }

}