package org.vd.vlogdeputatarb.controller.admin.user

import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.vd.vlogdeputatarb.data.user.User
import org.vd.vlogdeputatarb.util.enums.AuthProvider
import org.vd.vlogdeputatarb.util.enums.RoleType

data class AdminUserFilterRequest(

    val username: String? = null,
    val email: String? = null,
    val role: RoleType? = null,
    val blocked: Boolean? = null,
    val provider: AuthProvider? = null,
    val page: Int = 0,
    val size: Int = 20,
    val sort: String = "id",
    val id: Long? = null,
    val dir: Sort.Direction = Sort.Direction.DESC
) {
    fun toSpec(): Specification<User> {
        return Specification { root, _, cb ->
            val predicates = mutableListOf<Predicate>()

            username?.takeIf { it.isNotBlank() }?.let {
                predicates += cb.like(
                    cb.lower(root.get("username")),
                    "%${it.lowercase()}%"
                )
            }
            id?.let {
                predicates += cb.equal(root.get<Long>("id"), it)
            }
            email?.takeIf { it.isNotBlank() }?.let {
                predicates += cb.like(
                    cb.lower(root.get("email")),
                    "%${it.lowercase()}%"
                )
            }

            role?.let {
                predicates += cb.equal(root.get<RoleType>("role"), it)
            }

            blocked?.let {
                predicates += cb.equal(root.get<Boolean>("blocked"), it)
            }

            provider?.let {
                predicates += cb.equal(root.get<AuthProvider>("provider"), it)
            }


            cb.and(*predicates.toTypedArray())
        }
    }
    fun toPageable(): Pageable =
        PageRequest.of(page,
            size,
            Sort.by(dir, sort)
            )


    fun toParams(): Map<String, Any?> {
        val params = mutableMapOf<String, Any?>()

        id?.let { params["id"] = it }

        username
            ?.takeIf { it.isNotBlank() }
            ?.let { params["username"] = it }

        email
            ?.takeIf { it.isNotBlank() }
            ?.let { params["email"] = it }

        role?.let { params["role"] = it }

        provider?.let { params["provider"] = it }

        blocked?.let { params["blocked"] = it }


        // пагинация
        params["page"] = page
        params["size"] = size

        // сортировка
        sort?.let { params["sort"] = it }
        dir?.let { params["dir"] = it }

        return params
    }
}