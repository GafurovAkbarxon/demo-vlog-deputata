package org.vd.vlogdeputatarb.controller.admin.user

import org.vd.vlogdeputatarb.util.enums.AuthProvider
import org.vd.vlogdeputatarb.util.enums.RoleType
import java.time.LocalDateTime

data class AdminUserResponse(
    val id: Long,
    val username: String,
    val email: String?,
    val displayName: String?,
    val avatar: String?,
    val role: RoleType,
    val provider: AuthProvider,
    val blocked: Boolean,
    val createdAt: LocalDateTime,
)