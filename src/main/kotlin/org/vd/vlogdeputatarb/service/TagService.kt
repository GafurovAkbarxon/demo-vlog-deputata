package org.vd.vlogdeputatarb.service

import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.vd.vlogdeputatarb.data.tag.Tag
import org.vd.vlogdeputatarb.data.tag.TagRepository
import org.vd.vlogdeputatarb.util.enums.Language

@Service
class TagService(
    private val tagRepository: TagRepository
){
    fun getAll():List<String>{
        val all = tagRepository.findAll()
       return all.map { it.name }
    }

    fun getAllByLanguage(language:Language): List<Tag>{
      return  tagRepository.findAllByLanguage(language)
    }
    fun findAByNameAndLanguage(name:String,language:Language): Tag?{
     var tag = tagRepository.findByNameAndLanguage(name,language)
        if(tag==null){
            tag = tagRepository.findByCodeAndLanguage(normalizeTag(name),language)
        }
        return tag
    }


    @Transactional
    fun resolveTags(
        tags: List<String>,
        language: Language
    ): MutableSet<Tag> {
        val result = linkedSetOf<Tag>()

        tags.asSequence()
            .map { it.trim() }
            .filter { it.isNotBlank() }
            .distinct()
            .forEach { name ->

                val existingByName = tagRepository.findByNameAndLanguage(name, language)
                if (existingByName != null) {
                    result.add(existingByName)
                    return@forEach
                }

                val code = normalizeTag(name)
                require(code.isNotBlank()) { "Invalid tag name: $name" }

            val tag = tagRepository.findByCodeAndLanguage(code,language)
                ?: tagRepository.save(Tag(code,language,name)) // 🔥 ФИКС


            result.add(tag)
        }

        return result
    }

    private fun normalizeTag(input: String): String {
        val map = mapOf(
            'а' to "a",'б' to "b",'в' to "v",'г' to "g",'д' to "d",'е' to "e",'ё' to "e",
            'ж' to "zh",'з' to "z",'и' to "i",'й' to "y",'к' to "k",'л' to "l",
            'м' to "m",'н' to "n",'о' to "o",'п' to "p",'р' to "r",'с' to "s",
            'т' to "t",'у' to "u",'ф' to "f",'х' to "h",'ц' to "c",'ч' to "ch",
            'ш' to "sh",'щ' to "sh",'ы' to "y",'э' to "e",'ю' to "yu",'я' to "ya",

            // узбек кириллица
            'қ' to "q",'ў' to "u",'ғ' to "g",'ҳ' to "h"
        )

        val normalized = input
            .lowercase()
            // узбек латиница → нормализуем
            .replace("o‘", "o")
            .replace("g‘", "g")
            .replace("ʼ", "")
            .replace("’", "")
            .replace("'", "")

        val transliterated = buildString {
            normalized.forEach { ch ->
                append(map[ch] ?: ch)
            }
        }

        return transliterated
            .replace(Regex("[^a-z0-9]+"), "-") // заменяем на дефис
            .replace(Regex("^-+|-+$"), "")     // обрезаем дефисы по краям
    }
}
