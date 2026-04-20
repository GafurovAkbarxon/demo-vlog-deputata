package org.vd.vlogdeputatarb.security.service

import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService
import org.springframework.security.oauth2.core.OAuth2AuthenticationException
import org.springframework.security.oauth2.core.user.OAuth2User
import org.springframework.stereotype.Service
import org.vd.vlogdeputatarb.data.user.User
import org.vd.vlogdeputatarb.data.user.UserRepository
import org.vd.vlogdeputatarb.security.dto.UserPrincipal
import org.vd.vlogdeputatarb.service.FileService
import org.vd.vlogdeputatarb.util.enums.AuthProvider
import org.vd.vlogdeputatarb.util.enums.RoleType
import java.util.UUID

@Service
class CustomOAuth2UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val fileService: FileService,
) : OAuth2UserService<OAuth2UserRequest, OAuth2User> {

    override fun loadUser(userRequest: OAuth2UserRequest): OAuth2User {


        val oauthUser = DefaultOAuth2UserService().loadUser(userRequest)

        val email = oauthUser.getAttribute<String>("email")
            ?: throw OAuth2AuthenticationException("Email not found")
        var user = userRepository.findByEmail(email)



        if (user == null) {
            user = User(
                username = "google_${UUID.randomUUID()}",
                password = passwordEncoder.encode(UUID.randomUUID().toString()), // просто заглушка
                role = RoleType.USER,
                displayName = oauthUser.getAttribute<String>("name"),
                provider = AuthProvider.GOOGLE,
                email = email
            )
            val pictureUrl = oauthUser.getAttribute<String>("picture")
            if (pictureUrl != null) {
                user.avatarFilename = fileService.downloadAndSaveImage(pictureUrl)
            }

            userRepository.save(user)
        }

        if (user.blocked) {
            throw OAuth2AuthenticationException("User is blocked")
        }

        user.setOauth2Attributes(oauthUser.attributes)

      return UserPrincipal(user, oauthUser.attributes)
    }
}