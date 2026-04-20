package org.vd.vlogdeputatarb.security.dto

import org.springframework.security.authentication.AbstractAuthenticationToken

class Pre2faAuthenticationToken(//я чуть не опнял эту логику
    private val principal: UserPrincipal
) : AbstractAuthenticationToken(emptyList()) {

    init {
        super.setAuthenticated(false) //но запрешает с таким токеном заходить на другие страницы
    }

    override fun getPrincipal(): Any = principal
    override fun getCredentials(): Any? = null

    override fun setAuthenticated(isAuthenticated: Boolean) {
        if (isAuthenticated) {
            throw IllegalArgumentException("Cannot set this token to trusted again")
        }
    super.setAuthenticated(false)
    }

    override fun getName(): String = principal.username
}