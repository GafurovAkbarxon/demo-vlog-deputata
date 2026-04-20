package org.vd.vlogdeputatarb.data.wiki.enums

import java.time.LocalDate

enum class Zodiac(
    val code: String
) {

    ARIES("zodiac.aries"),
    TAURUS("zodiac.taurus"),
    GEMINI("zodiac.gemini"),
    CANCER("zodiac.cancer"),
    LEO("zodiac.leo"),
    VIRGO("zodiac.virgo"),
    LIBRA("zodiac.libra"),
    SCORPIO("zodiac.scorpio"),
    SAGITTARIUS("zodiac.sagittarius"),
    CAPRICORN("zodiac.capricorn"),
    AQUARIUS("zodiac.aquarius"),
    PISCES("zodiac.pisces");

    companion object {
        fun fromDate(date: LocalDate): Zodiac {
            val m = date.monthValue
            val d = date.dayOfMonth
            return when {
                (m == 3 && d >= 21) || (m == 4 && d <= 19) -> ARIES
                (m == 4 && d >= 20) || (m == 5 && d <= 20) -> TAURUS
                (m == 5 && d >= 21) || (m == 6 && d <= 20) -> GEMINI
                (m == 6 && d >= 21) || (m == 7 && d <= 22) -> CANCER
                (m == 7 && d >= 23) || (m == 8 && d <= 22) -> LEO
                (m == 8 && d >= 23) || (m == 9 && d <= 22) -> VIRGO
                (m == 9 && d >= 23) || (m == 10 && d <= 22) -> LIBRA
                (m == 10 && d >= 23) || (m == 11 && d <= 21) -> SCORPIO
                (m == 11 && d >= 22) || (m == 12 && d <= 21) -> SAGITTARIUS
                (m == 12 && d >= 22) || (m == 1 && d <= 19) -> CAPRICORN
                (m == 1 && d >= 20) || (m == 2 && d <= 18) -> AQUARIUS
                else -> PISCES
            }
        }
    }
}