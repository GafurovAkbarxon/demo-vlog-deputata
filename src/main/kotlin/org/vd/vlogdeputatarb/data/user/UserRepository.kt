package org.vd.vlogdeputatarb.data.user

import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.JpaSpecificationExecutor

interface UserRepository : JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    fun findByUsername(username: String): User?
    fun existsByUsername(username: String): Boolean

    fun findByEmail(email: String): User?
    fun existsByEmail(email: String): Boolean
}