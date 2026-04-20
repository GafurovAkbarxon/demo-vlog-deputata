package org.vd.vlogdeputatarb.security.dto

import org.springframework.security.authentication.AbstractAuthenticationToken

class TotpAuthenticationToken( //я чуть не опнял эту логику
    private val principal: UserPrincipal,
    private val code: String
) : AbstractAuthenticationToken(emptyList()) {

    init {
        isAuthenticated = false
    }

    override fun getPrincipal(): Any = principal
    override fun getCredentials(): Any = code
    override fun getName(): String = principal.username
}