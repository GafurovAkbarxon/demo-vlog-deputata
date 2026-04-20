package org.vd.vlogdeputatarb.service

import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.domain.Specification
import org.springframework.security.core.session.SessionRegistry
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.vd.vlogdeputatarb.controller.admin.user.AdminUserDetailsResponse
import org.vd.vlogdeputatarb.controller.admin.user.AdminUserResponse
import org.vd.vlogdeputatarb.data.comment.CommentRepository
import org.vd.vlogdeputatarb.data.user.User
import org.vd.vlogdeputatarb.data.user.UserRepository

@Service
class AdminUserService(
    private val userRepository: UserRepository,
    private val fileService: FileService,
    private val commentRepository: CommentRepository,
    private val sessionService: SessionService,
    private val persistentTokenRepository: PersistentTokenRepository
) {

    fun getUsers(
        spec: Specification<User>,
        pageable: Pageable
    ): Page<AdminUserResponse> {

        return userRepository.findAll(spec, pageable).map { user ->
            AdminUserResponse(
                id = user.id!!,
                username = user.username,
                email = user.email,
                displayName = user.displayName,
                avatar = user.avatarFilename,
                role = user.role,
                provider = user.provider,
                blocked = user.blocked,
                createdAt = user.createdAt,
            )
        }
    }

    @Transactional
      fun deleteUser(userId: Long) {
        val user =userRepository.getReferenceById(userId)
        val avatar =user.avatarFilename
        if (avatar!=null)
            fileService.deleteImage(avatar)
        commentRepository.deleteByUserId(userId)
        userRepository.deleteById(userId)
        sessionService.expireAllExcept(user, "")
        persistentTokenRepository.removeUserTokens(user.username)

    }

    @Transactional
     fun blockUser(userId: Long) {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        user.blocked = true
        sessionService.expireAllExcept(user, "")
        persistentTokenRepository.removeUserTokens(user.username)
    }

    @Transactional
     fun unblockUser(userId: Long) {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        user.blocked = false
    }




     fun getUserDetails(userId: Long): AdminUserDetailsResponse {
        val user = userRepository.findById(userId)
            .orElseThrow { IllegalArgumentException("User not found") }

        val comments = commentRepository.findAll()
            .filter { it.user.id == userId } // позже можно оптимизировать запросом

         val sessions = sessionService.getUserSessions(user, currentSessionId = "")

        return AdminUserDetailsResponse(
            id = user.id!!,
            username = user.username,
            email = user.email,
            displayName = user.displayName,
            role = user.role,
            provider = user.provider,
            blocked = user.blocked,
            createdAt = user.createdAt,
            comments = comments,
            sessions = sessions
            )
    }
}