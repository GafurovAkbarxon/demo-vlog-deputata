package org.vd.vlogdeputatarb.service

import org.springframework.security.authentication.AnonymousAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.vd.vlogdeputatarb.security.dto.UserPrincipal
import org.vd.vlogdeputatarb.controller.account.profile.dto.ChangePasswordRequest
import org.vd.vlogdeputatarb.controller.account.profile.dto.UpdateProfileRequest
import org.vd.vlogdeputatarb.data.user.User
import org.vd.vlogdeputatarb.data.user.UserRepository
import org.vd.vlogdeputatarb.util.enums.AuthProvider
import org.vd.vlogdeputatarb.util.enums.RoleType
import java.util.UUID

@Service
class UserService (
    private  val passwordEncoder: PasswordEncoder,
    private val userRepository: UserRepository,
    private val fileService: FileService
){
    @Transactional
     fun registerUser(username: String, rawPassword: String): User {

        if (existByUsername(username)) {
            throw IllegalArgumentException("Пользователь с таким именем уже существует")
        }

        val user = User(
            username = username,
            password = passwordEncoder.encode(rawPassword),
            displayName = "ghost_${UUID.randomUUID()}",
            provider = AuthProvider.LOCAL,
        )
        // роль USER по умолчанию (твой конструктор уже ставит USER, но на всякий случай)
        user.role = RoleType.USER
        return userRepository.save(user)
    }

     fun existByUsername(username:String): Boolean {
        return userRepository.existsByUsername(username)
    }


    @Transactional
     fun changePassword(dto: ChangePasswordRequest) {

        val user = getLoggedUser()

        // Google пользователь не может менять пароль
        if (user.provider == AuthProvider.GOOGLE) {
            throw IllegalStateException("Google users cannot change password")
        }

        // Проверка старого пароля
        if (!passwordEncoder.matches(dto.oldPassword, user.password)) {
            throw IllegalArgumentException("Старый пароль неверный")
        }
        //  Новый не должен совпадать со старым
        if (passwordEncoder.matches(dto.newPassword, user.password)) {
            throw IllegalArgumentException("Новый пароль должен отличаться от старого")
        }


        user.password = passwordEncoder.encode(dto.newPassword)
      userRepository.save(user)


    }

     fun getLoggedUser(): User {
        val principal = SecurityContextHolder.getContext().authentication.principal as UserPrincipal
        return principal.user
    }

    fun getLoggedUserOrNull(): User? {
        val auth = SecurityContextHolder.getContext().authentication ?: return null

        if (!auth.isAuthenticated) return null
        if (auth is AnonymousAuthenticationToken) return null

        val principal = auth.principal
        return if (principal is UserPrincipal) {
            principal.user
        } else {
            null
        }
    }
     fun updateProfile(userId: Long, dto: UpdateProfileRequest): User {
        val user = getLoggedUser()

        dto.displayName?.let { user.displayName = it }
        dto.avatarFile?.let { avatar ->
            if (!avatar.isEmpty) {
                val oldFilename = user.avatarFilename
                val newFilename = fileService.saveImage(avatar)
                if (!oldFilename.isNullOrBlank()) {
                    fileService.deleteImage(oldFilename)
                }
                user.avatarFilename = newFilename
            }
        }

        val updatedUser = userRepository.save(user)


        return updatedUser
    }
}