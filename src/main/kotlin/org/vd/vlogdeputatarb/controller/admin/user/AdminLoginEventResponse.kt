package org.vd.vlogdeputatarb.controller.admin.user

import org.vd.vlogdeputatarb.util.enums.AuthProvider
import java.time.LocalDateTime

data class AdminLoginEventResponse(
    val id: Long,
    val username: String?,
    val ip: String,
    val success: Boolean,
    val provider: AuthProvider,
    val browser: String?,
    val os: String?,
    val device: String?,
    val city: String?,
    val country: String?,
    val asn:Long?,
    val providerIp:String?,
    val createdAt: LocalDateTime
)