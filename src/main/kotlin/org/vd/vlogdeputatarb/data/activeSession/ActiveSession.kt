package org.vd.vlogdeputatarb.data.activeSession

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.FetchType
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.vd.vlogdeputatarb.data.user.User
import java.time.Instant

@Entity
@Table(
    name = "active_sessions",
    indexes = [
        Index(name = "idx_session_id", columnList = "sessionId"),
        Index(name = "idx_user_expired", columnList = "user_id, expired")
    ]
)
class ActiveSession(

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    var id: Long = 0,

    @Column(nullable = false, unique = true)
    var sessionId: String,

    @Column(nullable = false)
    var ip: String,

    var country: String? = null,
    var city: String? = null,

    var providerIp: String? = null,     // ASN / ISP

    var browser: String? = null,
    var os: String? = null,
    var device: String? = null,

    @Column(nullable = false)
    var createdAt: Instant = Instant.now(),

    @Column(nullable = false)
    var lastRequestAt: Instant = Instant.now(),

    @Column(nullable = false)
    var expired: Boolean = false,

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    var user: User
)