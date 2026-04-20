package org.vd.vlogdeputatarb.controller.web.persons.dto

import org.vd.vlogdeputatarb.controller.web.persons.enums.AliveFilter
import org.vd.vlogdeputatarb.controller.web.persons.enums.Season
import org.vd.vlogdeputatarb.data.wiki.enums.City
import org.vd.vlogdeputatarb.data.wiki.enums.PersonType
import org.vd.vlogdeputatarb.data.wiki.enums.SportStatus
import org.vd.vlogdeputatarb.data.wiki.enums.SportType
import org.vd.vlogdeputatarb.data.wiki.enums.Zodiac
import java.time.LocalDate

data class PersonFilterRequest(
    var fullName: String? = null,                 // поиск по имени/slug
    var alive: AliveFilter = AliveFilter.ALL,

    // возраст (в годах) — применим и к живым и к умершим (по логике ниже)
    var minAge: Int? = null,
    var maxAge: Int? = null,


    // дата рождения (точно / диапазон)
    var bornFrom: LocalDate? = null,
    var bornTo: LocalDate? = null,

    // год/месяц/сезон рождения
    var bornYear: Int? = null,              // 1900..2100
    var bornMonth: Int? = null,             // 1..12
    var bornSeason: Season? = null,

    var zodiac: Zodiac? = null,
    var birthPlace: City? = null,
    var type: PersonType? = null,

    // спорт
    var sportType: SportType? = null,
    var sportStatus: SportStatus? = null
)