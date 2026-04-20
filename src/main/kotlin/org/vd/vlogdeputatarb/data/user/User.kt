package org.vd.vlogdeputatarb.data.user

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import jakarta.persistence.Transient
import org.vd.vlogdeputatarb.util.enums.AuthProvider
import org.vd.vlogdeputatarb.util.enums.BaseModel
import org.vd.vlogdeputatarb.util.enums.RoleType
import java.time.LocalDateTime

@Table(name = "users")
@Entity
class User(
    @Column(nullable = false, unique = true)
    var username: String,
    var password: String,
    var avatarFilename: String? = null,
    @Enumerated(EnumType.STRING)
    @Column(name = "role")
    var role: RoleType = RoleType.USER,
    @Column(nullable = false, updatable = false)
    var createdAt: LocalDateTime = LocalDateTime.now(),
    var blocked: Boolean = false,
    var email: String? = null,        // для OAuthh
    var displayName: String? = null,   // имя для UI
    @Enumerated(EnumType.STRING)
    var provider: AuthProvider = AuthProvider.LOCAL,

    @Column(name = "two_factor_enabled", nullable = false)
    var twoFactorEnabled: Boolean = false,
    var twoFactorSecret: String? = null
) : BaseModel(){

    @Transient
    private var attributes: Map<String, Any> = emptyMap()
    fun setOauth2Attributes(attrs: Map<String, Any>) {
        this.attributes = attrs }
}