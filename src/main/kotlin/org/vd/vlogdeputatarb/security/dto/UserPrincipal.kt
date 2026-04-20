package org.vd.vlogdeputatarb.security.dto

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.oauth2.core.user.OAuth2User
import org.vd.vlogdeputatarb.data.user.User

class UserPrincipal (
    val user: User,
    private val oauth2Attributes: Map<String, Any> = emptyMap()
): UserDetails, OAuth2User {



    override fun getAuthorities(): Collection<GrantedAuthority> =
        listOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))
    override fun getPassword(): String = user.password
    override fun getUsername(): String= user.username

    override fun isAccountNonExpired(): Boolean = true
    override fun isAccountNonLocked(): Boolean {
        return !user.blocked
    }
    override fun isCredentialsNonExpired(): Boolean = true
    override fun isEnabled(): Boolean = true

    override fun getName() = user.id.toString()
    override fun getAttributes(): Map<String, Any> =
        oauth2Attributes

    }