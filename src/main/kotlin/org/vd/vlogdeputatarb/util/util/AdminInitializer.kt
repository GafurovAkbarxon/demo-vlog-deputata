package org.vd.vlogdeputatarb.util.util

import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component
import org.vd.vlogdeputatarb.data.user.User
import org.vd.vlogdeputatarb.util.enums.RoleType
import org.vd.vlogdeputatarb.data.user.UserRepository

@Component
class AdminInitializer(
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    @Value("\${app.admin.username:}")
    private val adminUsername: String,
    @Value("\${app.admin.password:}")
    private val adminPassword: String
) : ApplicationRunner {

    override fun run(args: ApplicationArguments) {
        if (adminUsername.isNullOrBlank() || adminPassword.isNullOrBlank()) {
            // не создаём администратора, если данные не заданы
            return
        }

        if (userRepository.findByUsername(adminUsername) == null) {
            val admin = User(
                username = adminUsername,
                password = passwordEncoder.encode(adminPassword),
                displayName = "admin"
//                selectedLang = Language.RU
            )
            admin.role = RoleType.ADMIN
            userRepository.save(admin)
            // НЕ логируем пароль, только факт создания
            println("Admin user created with your UE abd PD")
        }
    }
}