package org.vd.vlogdeputatarb.data.loginEvent

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.FetchType
import jakarta.persistence.Index
import jakarta.persistence.JoinColumn
import jakarta.persistence.ManyToOne
import jakarta.persistence.Table
import org.vd.vlogdeputatarb.data.user.User
import org.vd.vlogdeputatarb.util.enums.AuthProvider
import org.vd.vlogdeputatarb.util.enums.BaseModel
import java.time.LocalDateTime

@Entity
@Table(
    name = "login_events", indexes = [
        Index(name = "idx_login_event_ip", columnList = "ip"),
        Index(name = "idx_login_event_username", columnList = "username"),
        Index(name = "idx_login_event_created", columnList = "createdAt"),
        Index(name = "idx_login_event_created_success", columnList = "createdAt, success"),
        Index(name = "idx_login_event_ip_created", columnList = "ip, createdAt"),
        Index(name = "idx_login_event_username_created", columnList = "username, createdAt"),
        Index(name = "idx_login_event_browser", columnList = "browser"),
        Index(name = "idx_login_event_os", columnList = "os"),
        Index(name = "idx_login_event_device", columnList = "device"),
        Index(name = "idx_login_event_city", columnList = "city"),
        Index(name = "idx_login_event_country", columnList = "country")
    ]
)
class LoginEvent(

    @Column(nullable = true)
    val username: String?,


    @Column(nullable = false)
    val ip: String,

    @Column(nullable = false)
    val success: Boolean,

    @Enumerated(EnumType.STRING)
    val provider: AuthProvider,

    @Column(length = 512)
    val userAgent: String?,
    val browser: String?,   // Chrome 122
    val os: String?,        // Windows 10
    val device: String?,    // Desktop / Mobile / iPhone
    val city: String?,
    val country: String?,
    val asn: Long?,
    val providerIp: String?,
    val createdAt: LocalDateTime = LocalDateTime.now()
) : BaseModel()


