package org.vd.vlogdeputatarb.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.core.Authentication
import org.springframework.security.core.session.SessionRegistry
import org.springframework.security.core.session.SessionRegistryImpl
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.authentication.RememberMeServices
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository
import org.springframework.security.web.authentication.session.ChangeSessionIdAuthenticationStrategy
import org.springframework.security.web.authentication.session.CompositeSessionAuthenticationStrategy
import org.springframework.security.web.authentication.session.ConcurrentSessionControlAuthenticationStrategy
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy
import org.springframework.security.web.session.HttpSessionEventPublisher
import org.springframework.web.client.RestTemplate
import org.springframework.web.filter.ForwardedHeaderFilter
import org.vd.vlogdeputatarb.security.filter.TotpAuthenticationFilter
import org.vd.vlogdeputatarb.security.handler.TotpFailureHandler
import org.vd.vlogdeputatarb.security.handler.TotpSuccessHandler
import org.vd.vlogdeputatarb.security.service.RoleAwareRememberMeServices
import org.vd.vlogdeputatarb.security.dto.UserPrincipal
import org.vd.vlogdeputatarb.util.enums.RoleType
import org.vd.vlogdeputatarb.util.util.properties.RememberMeProperties
import ua_parser.Parser
import javax.sql.DataSource

@Configuration
class CoreBeansConfig {

    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun restTemplate(): RestTemplate {
        return RestTemplate()
    }
    @Bean
    fun uaParser(): Parser = Parser()


    /**
     * нужно чтобы извлекать настощий Ip
     */
    @Bean
    fun persistentTokenRepository(dataSource: DataSource): PersistentTokenRepository {
        val repo = JdbcTokenRepositoryImpl()
        repo.setDataSource(dataSource)
        // repo.setCreateTableOnStartup(true) // только один раз!
        return repo
    }
    /**
     * нужно чтобы спринг сам мог деактивировать похищенные токены
     */



    @Bean
    fun sessionRegistry(): SessionRegistry =
        SessionRegistryImpl()
    @Bean
    fun httpSessionEventPublisher(): HttpSessionEventPublisher {
        return HttpSessionEventPublisher()
    }
    /** когда сессия
     * уничтожается или логоут или  истек
     * занимается удаление из sessionRegistry
     * без них лимит сессий работает криво*/

    @Bean
    fun customRememberMeServices(
        props: RememberMeProperties,
        userDetailsService: UserDetailsService,
        tokenRepo: PersistentTokenRepository
    ): RememberMeServices {
        return RoleAwareRememberMeServices(props.key, userDetailsService, tokenRepo).apply {
            setCookieName("REMEMBER_ME")
            setParameter("remember-me")
            setTokenValiditySeconds(60 * 60 * 24 * 14)
            setAlwaysRemember(false)
            setUseSecureCookie(true)
        }
    }

    /**
     * все этип поля можно настроить в цепочке FilterChain для RememberMe,
     * но мы выбрали создать кастомный
     */

@Bean
fun customSessionAuthenticationStrategy(
    sessionRegistry: SessionRegistry
): SessionAuthenticationStrategy {
    val concurrentStrategy =
        object : ConcurrentSessionControlAuthenticationStrategy(sessionRegistry) {
            override fun getMaximumSessionsForThisUser(authentication: Authentication): Int {
                val principal = authentication.principal as? UserPrincipal
                    ?: return -1
                return if (principal.user.role == RoleType.ADMIN) 1 else -1
            }
        }.apply { setExceptionIfMaximumExceeded(false) }

    return CompositeSessionAuthenticationStrategy(
        listOf(
            concurrentStrategy,
            ChangeSessionIdAuthenticationStrategy(),//До спринг Секюрити 5 называлось SessionFixationProtectionStrategy()
            RegisterSessionAuthenticationStrategy(sessionRegistry)
        )
    )
}
    /**
     * все этип поля можно настроить в цепочке FilterChain для SessionManagement,
     * но мы выбрали создать кастомный
     */
}