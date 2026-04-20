package org.vd.vlogdeputatarb.controller.web.persons

import jakarta.persistence.criteria.CriteriaBuilder
import jakarta.persistence.criteria.Expression
import jakarta.persistence.criteria.Path
import jakarta.persistence.criteria.Predicate
import org.hibernate.query.sqm.TemporalUnit
import org.springframework.data.jpa.domain.Specification
import org.vd.vlogdeputatarb.controller.web.persons.dto.PersonFilterRequest
import org.vd.vlogdeputatarb.controller.web.persons.enums.AliveFilter
import org.vd.vlogdeputatarb.controller.web.persons.enums.Season
import org.vd.vlogdeputatarb.data.wiki.Person
import java.time.LocalDate

object PersonSpecifications {

    fun byFilter(filter: PersonFilterRequest, now: LocalDate = LocalDate.now()): Specification<Person> =
        Specification { root, _, cb ->

            // ✅ типизируем поля один раз
            val birthDate = root.get<LocalDate>("birthDate")
            val deathDate = root.get<LocalDate?>("deathDate")
            val deathAgeYears = root.get<Int?>("deathAgeYears")

            val ps = mutableListOf<Predicate>()

            // --- q: slug / ruName / uzName (case-insensitive)
            filter.fullName?.trim()?.takeIf { it.isNotEmpty() }?.let { q ->
                val like = "%${q.lowercase()}%"
                ps += cb.or(
                    cb.like(cb.lower(root.get("slug")), like),
                    cb.like(cb.lower(root.get("ruName")), like),
                    cb.like(cb.lower(root.get("uzName")), like),
                )
            }

            // --- alive/dead
            when (filter.alive) {
                AliveFilter.ALIVE -> ps += cb.isNull(deathDate)
                AliveFilter.DEAD -> ps += cb.isNotNull(deathDate)
                AliveFilter.ALL -> {}
            }

            // --- exact enums
            filter.type?.let { ps += cb.equal(root.get<Any>("type"), it) }
            filter.birthPlace?.let { ps += cb.equal(root.get<Any>("birthPlace"), it) }
            filter.zodiac?.let { ps += cb.equal(root.get<Any>("zodiac"), it) }
            filter.sportType?.let { ps += cb.equal(root.get<Any>("sportType"), it) }
            filter.sportStatus?.let { ps += cb.equal(root.get<Any>("sportStatus"), it) }

            // --- born date range
            filter.bornFrom?.let { ps += cb.greaterThanOrEqualTo(birthDate, it) }
            filter.bornTo?.let { ps += cb.lessThanOrEqualTo(birthDate, it) }

            // --- bornYear (index-friendly)
            filter.bornYear?.let { y ->
                val from = LocalDate.of(y, 1, 1)
                val to = LocalDate.of(y, 12, 31)
                ps += cb.between(birthDate, from, to)
            }

            // --- bornMonth via Postgres date_part('month', birthDate) -> double precision
            filter.bornMonth?.let { m ->
                val monthExpr = cb.function("date_part", Double::class.java, cb.literal("month"), birthDate)
                ps += cb.equal(monthExpr, m.toDouble())
            }

            // --- bornSeason via date_part('month', birthDate)
            filter.bornSeason?.let { season ->
                val months = when (season) {
                    Season.WINTER -> listOf(12, 1, 2)
                    Season.SPRING -> listOf(3, 4, 5)
                    Season.SUMMER -> listOf(6, 7, 8)
                    Season.AUTUMN -> listOf(9, 10, 11)
                }.map { it.toDouble() }

                val monthExpr = cb.function("date_part", Double::class.java, cb.literal("month"), birthDate)
                ps += monthExpr.`in`(months)
            }

            // --- age filters (min/max) for ALIVE/DEAD/ALL
            if (filter.minAge != null || filter.maxAge != null) {
                val minA = filter.minAge
                val maxA = filter.maxAge

                when (filter.alive) {
                    AliveFilter.ALIVE -> {
                        val (from, to) = birthDateRangeForAge(now, minA, maxA)
                        ps += cb.isNull(deathDate)
                        from?.let { ps += cb.greaterThanOrEqualTo(birthDate, it) }
                        to?.let { ps += cb.lessThanOrEqualTo(birthDate, it) }
                    }

                    AliveFilter.DEAD -> {
                        ps += cb.isNotNull(deathAgeYears)
                        minA?.let { ps += cb.greaterThanOrEqualTo(deathAgeYears, it) }
                        maxA?.let { ps += cb.lessThanOrEqualTo(deathAgeYears, it) }
                    }

                    AliveFilter.ALL -> {
                        val alivePs = mutableListOf<Predicate>()
                        run {
                            val (from, to) = birthDateRangeForAge(now, minA, maxA)
                            alivePs += cb.isNull(deathDate)
                            from?.let { alivePs += cb.greaterThanOrEqualTo(birthDate, it) }
                            to?.let { alivePs += cb.lessThanOrEqualTo(birthDate, it) }
                        }

                        val deadPs = mutableListOf<Predicate>()
                        run {
                            deadPs += cb.isNotNull(deathAgeYears)
                            minA?.let { deadPs += cb.greaterThanOrEqualTo(deathAgeYears, it) }
                            maxA?.let { deadPs += cb.lessThanOrEqualTo(deathAgeYears, it) }
                        }

                        ps += cb.or(
                            cb.and(*alivePs.toTypedArray()),
                            cb.and(*deadPs.toTypedArray())
                        )
                    }
                }
            }

            cb.and(*ps.toTypedArray())
        }

    private fun birthDateRangeForAge(
        now: LocalDate,
        minAge: Int?,
        maxAge: Int?
    ): Pair<LocalDate?, LocalDate?> {
        val to = minAge?.let { now.minusYears(it.toLong()) }
        val from = maxAge?.let { now.minusYears((it + 1).toLong()).plusDays(1) }
        return from to to
    }
}