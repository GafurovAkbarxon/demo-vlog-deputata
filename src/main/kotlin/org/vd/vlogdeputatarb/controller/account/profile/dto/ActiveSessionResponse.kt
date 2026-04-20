package org.vd.vlogdeputatarb.controller.account.profile.dto

import java.time.Instant

data class ActiveSessionResponse(
    val sessionId: String,
    val ip: String,
    val country: String?,
    val city: String?,
    val providerIp: String?,
    val browser: String?,
    val os: String?,
    val device: String?,
    val createdAt: Instant,
    val lastRequestAt: Instant,
    val current: Boolean
)