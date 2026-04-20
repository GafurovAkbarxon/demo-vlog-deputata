package org.vd.vlogdeputatarb.controller.web.persons.dto

import org.vd.vlogdeputatarb.data.wiki.enums.City
import org.vd.vlogdeputatarb.data.wiki.enums.PersonType
import org.vd.vlogdeputatarb.data.wiki.enums.SportStatus
import org.vd.vlogdeputatarb.data.wiki.enums.SportType
import org.vd.vlogdeputatarb.data.wiki.enums.Zodiac
import java.time.LocalDate

data class PersonResponse(
    val id: Long,
    val slug: String,
    val name: String,
    val birthPlace: City,
    val birthDate: LocalDate,
    val zodiac: Zodiac,
    val isAlive: Boolean,
    val ageYears: Int,
    val deathDate: LocalDate?,
    val deathAgeYears: Int?,
    val type: PersonType,
    val sportType: SportType?,
    val sportStatus: SportStatus?,
    val imageFilename: String,
    val alt: String,
    val externalUrl: String?,
    val description: String?
)