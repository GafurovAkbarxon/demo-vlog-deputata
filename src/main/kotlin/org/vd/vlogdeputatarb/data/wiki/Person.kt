package org.vd.vlogdeputatarb.data.wiki

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Transient
import jakarta.persistence.Index
import jakarta.persistence.Lob
import jakarta.persistence.PrePersist
import jakarta.persistence.PreUpdate
import jakarta.persistence.Table
import org.vd.vlogdeputatarb.data.wiki.enums.City
import org.vd.vlogdeputatarb.data.wiki.enums.PersonType
import org.vd.vlogdeputatarb.data.wiki.enums.SportStatus
import org.vd.vlogdeputatarb.data.wiki.enums.SportType
import org.vd.vlogdeputatarb.data.wiki.enums.Zodiac
import org.vd.vlogdeputatarb.util.enums.BaseModel
import org.vd.vlogdeputatarb.util.enums.Language
import java.time.LocalDate
import java.time.Period

@Entity
@Table(
    name = "persons",
    indexes = [
        Index(name = "ix_person_slug", columnList = "slug", unique = true),
        Index(name = "ix_person_birth_place", columnList = "birthPlace"),
        Index(name = "ix_person_type", columnList = "type"),
        Index(name = "ix_person_sport", columnList = "sportType"),
        Index(name = "ix_person_sport_status", columnList = "sportStatus"),
        Index(name = "ix_person_zodiac", columnList = "zodiac"),
        Index(name = "ix_person_birth_date", columnList = "birthDate"),
        Index(name = "ix_person_death_date", columnList = "deathDate"),
    ]
)
class Person(

    @Column(nullable = false, unique = true)
    var slug: String,

    @Column(nullable = false)
    var ruName: String,
    @Column(nullable = false)
    var uzName: String,
    @Column(nullable = false)
    var alt: String,


    @Column(nullable = false)
    var imageFilename: String,


    @Column(nullable = false)
    var birthDate: LocalDate,

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var zodiac: Zodiac = Zodiac.ARIES,



    var deathDate: LocalDate? = null,

    @Column
    var deathAgeYears: Int? = null,



    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var birthPlace: City,


    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    var type: PersonType,


    @Lob
    var ruDescription: String? = null,
    @Lob
    var uzDescription: String? = null,


    var externalUrl: String? = null,


    @Enumerated(EnumType.STRING)
    var sportType: SportType? = null,

    @Enumerated(EnumType.STRING)
    var sportStatus: SportStatus? = null,



    ): BaseModel() {


    @get:Transient
    val isAlive: Boolean
        get() = deathDate == null

    fun ageYears(now: LocalDate = LocalDate.now()): Int {
        val end = deathDate ?: now
        return Period.between(birthDate, end).years
    }

    @PrePersist
    @PreUpdate
    fun recomputeDerivedFields() {
        zodiac = Zodiac.fromDate(birthDate)
        deathAgeYears = deathDate?.let { Period.between(birthDate, it).years }

        // Мини-валидации (чтобы не было мусора в БД)
        if (deathDate != null && deathDate!!.isBefore(birthDate)) {
            throw IllegalStateException("deathDate cannot be before birthDate")
        }

        if (type == PersonType.ATHLETE) {
            if (sportType == null) throw IllegalStateException("sportDiscipline is required for ATHLETE")
            if (sportStatus == null) throw IllegalStateException("sportStatus is required for ATHLETE")
        } else {
            sportType = null
            sportStatus = null
        }
    }

    fun name(lang: Language): String = if (lang == Language.UZ) uzName else ruName
    fun haveDescription()= (uzDescription!=null && ruDescription!=null)

    fun description(lang: Language): String {
        if (haveDescription()) {
            if (lang == Language.UZ)
                return uzDescription!!
            else if (lang == Language.RU)
                return ruDescription!!
        }
        return ""
    }
}










