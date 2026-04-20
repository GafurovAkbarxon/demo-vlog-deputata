package org.vd.vlogdeputatarb.util.enums


enum class Language(val code: String,val ogLocale:String) {
    RU("ru", "ru_RU"),
    UZ("uz", "uz_UZ");

    companion object {
        fun from(code: String): Language =
            entries.firstOrNull { it.code == code.lowercase() }
                ?: RU
    }
}