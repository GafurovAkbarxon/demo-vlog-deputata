package org.vd.vlogdeputatarb.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.AuthenticationProvider
import org.springframework.security.authentication.ProviderManager
import org.springframework.security.authorization.AuthorizationDecision
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.RememberMeServices
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy
import org.springframework.security.web.context.HttpSessionSecurityContextRepository
import org.springframework.security.web.context.SecurityContextRepository
import org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository
import org.springframework.security.web.header.writers.StaticHeadersWriter
import org.vd.vlogdeputatarb.security.dto.Pre2faAuthenticationToken
import org.vd.vlogdeputatarb.security.filter.SessionTrackingFilter
import org.vd.vlogdeputatarb.security.filter.TotpAuthenticationFilter
import org.vd.vlogdeputatarb.security.handler.CustomAuthFailureHandler
import org.vd.vlogdeputatarb.security.handler.CustomAuthSuccessHandler
import org.vd.vlogdeputatarb.security.handler.CustomLogoutHandler
import org.vd.vlogdeputatarb.security.handler.TotpFailureHandler
import org.vd.vlogdeputatarb.security.handler.TotpSuccessHandler
import org.vd.vlogdeputatarb.security.provider.TotpAuthenticationProvider
import org.vd.vlogdeputatarb.security.service.CustomOAuth2UserService

@Configuration
class SecurityBeanConfig(
    private val customOAuth2UserService: CustomOAuth2UserService,

    private val sessionTrackingFilter: SessionTrackingFilter,

    private val customAuthenticationProvider: AuthenticationProvider,
    private val totpAuthenticationProvider: TotpAuthenticationProvider,

    private val failureHandler: CustomAuthFailureHandler,
    private val successHandler: CustomAuthSuccessHandler,
    private val customLogoutHandler: CustomLogoutHandler,
    private val totpFailureHandler: TotpFailureHandler,
    private val totpSuccessHandler: TotpSuccessHandler,

    private val customSessionAuthenticationStrategy: SessionAuthenticationStrategy,
    private val customRememberMeServices: RememberMeServices,
    ) {

    @Bean
    fun authenticationManager(): AuthenticationManager =
        ProviderManager(listOf(customAuthenticationProvider, totpAuthenticationProvider))


    @Bean
    fun totpAuthenticationFilter(
        http: HttpSecurity,
        authenticationManager: AuthenticationManager,
        totpFailureHandler: TotpFailureHandler,
        totpSuccessHandler: TotpSuccessHandler,
        customSessionAuthenticationStrategy: SessionAuthenticationStrategy
    ): TotpAuthenticationFilter {

        val repo = http.getSharedObject(SecurityContextRepository::class.java)
            ?: HttpSessionSecurityContextRepository() // fallback

        return TotpAuthenticationFilter(authenticationManager, repo, customSessionAuthenticationStrategy).apply {
            setAuthenticationFailureHandler(totpFailureHandler)
            setAuthenticationSuccessHandler(totpSuccessHandler)
        }
    }
    @Bean
    fun filterChain(http: HttpSecurity,
                    authenticationManager: AuthenticationManager,
                    totpAuthenticationFilter: TotpAuthenticationFilter): SecurityFilterChain {

        http

            .authorizeHttpRequests { auth ->
                auth.requestMatchers("/auth/2fa", "/auth/2fa/verify")
                    .access { a, _ -> AuthorizationDecision(a.get() is Pre2faAuthenticationToken) }
                auth.requestMatchers(
                    "/auth/login",
                    "/auth/register",
                    "/login",
                    "/register",
                    "/",
                    "/favicon.ico",
                    "/home",
                    "/error",
                    "/access-denied",
                    "/ru/**",
                    "/uz/**",
                    "/css/**",
                    "/js/**",
                    "/image/**",
                    "/uploads/**",
                    "/robots.txt",
                    "/sitemap.xml",
                    "/news-sitemap.xml"
                ).permitAll()

                //admin
                auth.requestMatchers(
                    "/ru/admin/**"
                ).hasRole("ADMIN")

                //profile
                auth.requestMatchers(
                    HttpMethod.GET,
                    "/ru/profile/**",
                    "/uz/profile/**"
                ).authenticated()

                auth.requestMatchers(
                    HttpMethod.POST,
                    "/comments/add",
                    "/ru/profile/**",
                    "/uz/profile/**"
                ).authenticated()
                auth.anyRequest().authenticated()


            }
            .formLogin {
                it.loginPage("/auth/login")             /**Cоздаем свою html*/
                    .loginProcessingUrl("/auth/login")    /**Cоздаем свой котнроллер*/
                    .defaultSuccessUrl("/ru/home", true)
                    .failureUrl("/auth/login?error") /**Куда пердавать ошибку*/
                    .permitAll()
                it.failureHandler(failureHandler)
                it.successHandler(successHandler)

            }
            /**
             * Это уже создает дефолтный
             * UsernamePasswordAuthenticationFilter который слушает POST /login извлекает из него Us Ps
             * Дефолтно использует DUOAuthenticationProvider если есть реализация UserDetailsService
             * Создает AuthenticationManager
             * GET POST Logout
             * SecurityContextRepository -HttpSessionSecurityContextRepository {если jwt поменяй на NullSecurityContextRepository}
             * SessionAuthenticationStrategy если не настроил  применяется упршенная ее версия  которая имеет лишь ChangeSessionAuthenticationStrategy которая лишь имеет защиту от essionFixation
             */

            .logout { logout ->
                logout.logoutUrl("/logout") /**Cоздаем свою html*/
                    .logoutSuccessUrl("/ru/home")
                    .logoutSuccessHandler(customLogoutHandler)
                    .invalidateHttpSession(true)
                    .deleteCookies("JSESSIONID","REMEMBER_ME")
            }
            .csrf {  csrf ->
                csrf.csrfTokenRepository(HttpSessionCsrfTokenRepository())
            }
            /** 1)CookieCsrfTokenRepository если только имеет клиент SPA и  все еще хотим использовать сессии  а дсотуп кним через SPA получает через куки но нужно настроить безопасность
             *  2)если клиентов будет много тоесть и адроид то лучше выключить csrf и пологаться на JWT настроив рефреш токен*/
           .oauth2Login {
                it.loginPage("/auth/login")
                it.userInfoEndpoint { u -> u.userService(customOAuth2UserService) }
                it.defaultSuccessUrl("/ru/home", true)
                it.failureHandler(SimpleUrlAuthenticationFailureHandler())
            }
            .addFilterAfter(sessionTrackingFilter, UsernamePasswordAuthenticationFilter::class.java)
            .addFilterAfter(totpAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
            .authenticationManager(authenticationManager)
//            .authenticationProvider(customAuthenticationProvider)
//            .authenticationProvider(totpAuthenticationProvider)
            /** AuthenticationManager  создать и отмечать их эти провадеров не нужно  оно само под капотом сделается*/
            .sessionManagement { session ->
                session.invalidSessionUrl("/auth/login?expired")
                session.sessionAuthenticationStrategy (customSessionAuthenticationStrategy)
//                session.sessionFixation { it.migrateSession() } // это дефолт поведение которое и создается и без этого кода ChangeSessionAuthenticationStrategy
//                session.maximumSessions(-1)                     // это подкючение в SessionAuthentication- ConcurentSessionControlAS  для контроля лимита,можно ли логинится- unlimited для обычных
//                    .maxSessionsPreventsLogin(false)            // для убивания старых
//                    .sessionRegistry(sessionRegistry)           // и если хотим ConcurentSessionControlAS  то нужен  еще RegisterSessionControlAS чтобы знать где  регитстровать тоесть просто бин sessionRegistry указать
            }
            /** Если JWT session.sessionCreationpolicy.STATELESS */
            .exceptionHandling {
                it.authenticationEntryPoint { _, response, _ ->

                    val auth = SecurityContextHolder.getContext().authentication
                    if (auth is Pre2faAuthenticationToken) {
                        response.sendRedirect("/auth/2fa")
                    } else {
                        response.sendRedirect("/auth/login")
                    }
                }
                it.accessDeniedHandler { _, response, _ ->
                    response.sendRedirect("/access-denied")
                }
            }

            .rememberMe {
                it.rememberMeServices(customRememberMeServices)
            }

            .headers { headers ->
                headers.frameOptions { it.sameOrigin() }
                headers.httpStrictTransportSecurity { hsts ->
                    hsts.includeSubDomains(true).maxAgeInSeconds(31536000)
                }
                headers.contentSecurityPolicy {
                    it.policyDirectives(
                        "default-src 'self'; " +
                                "style-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://cdn.jsdelivr.net/npm/bootstrap@5.3.2/dist/css/ https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/ https://cdn.jsdelivr.net/npm/bootstrap-icons@1.11.1/font/ https://fonts.googleapis.com; " +
                                "font-src 'self' https://cdn.jsdelivr.net https://fonts.gstatic.com data:; " +
                                "script-src 'self' 'unsafe-inline' https://cdn.jsdelivr.net https://code.jquery.com https://cdn.ckeditor.com https://challenges.cloudflare.com; " +
                                "frame-src https://challenges.cloudflare.com; " +
                                "connect-src 'self' https://challenges.cloudflare.com https://cdn.jsdelivr.net data: blob: ws://gc.kis.v2.scr.kaspersky-labs.com; " +
                                "img-src 'self'  https://www.gstatic.com https://*.googleusercontent.com data: blob:;"
                    )
                }
                headers.addHeaderWriter(StaticHeadersWriter("X-Content-Type-Options", "nosniff"))
            }

        return http.build()
    }

}