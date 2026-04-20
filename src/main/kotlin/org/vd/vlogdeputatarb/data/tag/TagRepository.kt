package org.vd.vlogdeputatarb.data.tag

import org.springframework.data.jpa.repository.JpaRepository
import org.vd.vlogdeputatarb.util.enums.Language



interface TagRepository : JpaRepository<Tag, Long> {
   fun findByNameAndLanguage(name:String, language:Language):Tag?
   fun findByCodeAndLanguage(code: String, language: Language): Tag?
   fun findAllByLanguage(language: Language): List<Tag>

}