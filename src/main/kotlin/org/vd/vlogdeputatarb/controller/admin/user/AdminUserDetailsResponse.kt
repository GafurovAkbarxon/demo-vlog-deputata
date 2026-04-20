package org.vd.vlogdeputatarb.controller.admin.user

import org.vd.vlogdeputatarb.data.comment.Comment
import org.vd.vlogdeputatarb.controller.account.profile.dto.ActiveSessionResponse
import org.vd.vlogdeputatarb.util.enums.AuthProvider
import org.vd.vlogdeputatarb.util.enums.RoleType
import java.time.LocalDateTime

data class AdminUserDetailsResponse(
    val id: Long,
    val username: String,
    val email: String?,
    val displayName: String?,
    val role: RoleType,
    val provider: AuthProvider,
    val blocked: Boolean,
    val createdAt: LocalDateTime?,
    val comments: List<Comment>,
    val sessions: List<ActiveSessionResponse> = emptyList()
)