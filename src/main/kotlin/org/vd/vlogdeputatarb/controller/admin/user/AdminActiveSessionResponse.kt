package org.vd.vlogdeputatarb.controller.admin.user

import java.time.Instant

data class AdminActiveSessionResponse(
    val sessionId: String,
    val lastRequestAt: Instant?,
    val ip: String?,
    val browser: String,
    val os: String,
    val device: String,
    val expired: Boolean
)