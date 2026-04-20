package org.vd.vlogdeputatarb.data.wiki

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface PersonRepository : JpaRepository<Person, Long>, JpaSpecificationExecutor<Person> {
    fun existsBySlug(slug: String): Boolean
    fun existsBySlugAndIdNot(slug: String, id: Long): Boolean
}