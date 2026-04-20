package org.vd.vlogdeputatarb.controller.admin.user

import jakarta.persistence.criteria.Predicate
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.jpa.domain.Specification
import org.vd.vlogdeputatarb.data.loginEvent.LoginEvent
import org.vd.vlogdeputatarb.util.enums.AuthProvider
import java.time.LocalDateTime

data class AdminLoginEventFilterRequest(

    val username: String? = null,
    val ip: String? = null,
    val success: Boolean? = null,
    val provider: AuthProvider? = null,
    val browser: String? = null,
    val os: String? = null,
    val device: String? = null,
    val city: String? = null,
    val country: String? = null,
    val asn:Long?=null,
    val providerIp:String?=null,

    val page: Int = 0,
    val size: Int = 20,
    val sort: String = "createdAt",
    val dir: Sort.Direction = Sort.Direction.DESC,
    val from: LocalDateTime? = null,
    val to: LocalDateTime? = null,

    ) {

    private val allowedSorts = setOf(
        "createdAt",
        "username",
        "ip",
        "provider",
        "success",
        "browser",
        "os",
        "device",
        "city",
        "country",
        "asn",
        "providerIp"
    )

    fun toPageable(): Pageable {
        val safeSort = if (sort in allowedSorts) sort else "createdAt"
        val safeSize = size.coerceIn(1, 100)
        return PageRequest.of(page.coerceAtLeast(0), safeSize, Sort.by(dir, safeSort))
    }
    fun toSpec(): Specification<LoginEvent> {
        return Specification { root, _, cb ->

            val predicates = mutableListOf<Predicate>()

            //sql optimized fullscan
            fun like(field: String, value: String) {
                predicates += cb.like(
                    cb.lower(root.get(field)),
                    "%${value.lowercase()}%"
                )
            }

            username?.takeIf { it.isNotBlank() }?.let {
                like("username", it)
            }
            from?.let {
                predicates += cb.greaterThanOrEqualTo(root.get("createdAt"), it)
            }

            to?.let {
                predicates += cb.lessThanOrEqualTo(root.get("createdAt"), it)
            }
            ip?.takeIf { it.isNotBlank() }?.let {
                like("ip", it)
            }
            city?.takeIf { it.isNotBlank() }?.let {
                like("city", it)
            }
            country?.takeIf { it.isNotBlank() }?.let {
                like("country", it)
            }
            providerIp?.takeIf { it.isNotBlank() }?.let {
                like("providerIp", it)
            }
            asn?.let {
                predicates += cb.equal(root.get<Long>("asn"), it)
            }
            browser?.takeIf { it.isNotBlank() }?.let {
                like("browser", it)
            }

            os?.takeIf { it.isNotBlank() }?.let {
                like("os", it)
            }

            device?.takeIf { it.isNotBlank() }?.let {
                like("device", it)
            }

            success?.let {
                predicates += cb.equal(root.get<Boolean>("success"), it)
            }

            provider?.let {
                predicates += cb.equal(root.get<AuthProvider>("provider"), it)
            }

            cb.and(*predicates.toTypedArray())
        }

}
    fun toParams(): Map<String, Any?> {
        val params = mutableMapOf<String, Any?>()

        username?.takeIf { it.isNotBlank() }?.let { params["username"] = it }
        ip?.takeIf { it.isNotBlank() }?.let { params["ip"] = it }
        success?.let { params["success"] = it }
        provider?.let { params["provider"] = it }
        browser?.takeIf { it.isNotBlank() }?.let { params["browser"] = it }
        os?.takeIf { it.isNotBlank() }?.let { params["os"] = it }
        device?.takeIf { it.isNotBlank() }?.let { params["device"] = it }
        city?.takeIf { it.isNotBlank() }?.let { params["city"] = it }
        country?.takeIf { it.isNotBlank() }?.let { params["country"] = it }
        asn?.let { params["asn"] = it }
        providerIp?.takeIf { it.isNotBlank() }?.let { params["providerIp"] = it }
        from?.let { params["from"] = it }
        to?.let { params["to"] = it }

        params["page"] = page
        params["size"] = size
        params["sort"] = sort
        params["dir"] = dir

        return params
    }
}