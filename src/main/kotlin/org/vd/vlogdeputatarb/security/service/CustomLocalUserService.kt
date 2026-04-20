package org.vd.vlogdeputatarb.security.service

import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.core.userdetails.UsernameNotFoundException
import org.springframework.stereotype.Service
import org.vd.vlogdeputatarb.data.user.UserRepository
import org.vd.vlogdeputatarb.security.dto.UserPrincipal

@Service
class CustomLocalUserService(
    private val userRepository: UserRepository,
): UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails {
        val user= userRepository.findByUsername(username)
            ?: throw UsernameNotFoundException("Bad credentials")
        return UserPrincipal(user)
    }

}