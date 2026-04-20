package org.vd.vlogdeputatarb.service

import com.github.benmanes.caffeine.cache.Caffeine
import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Refill
import org.springframework.http.HttpEntity
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.stereotype.Service
import org.springframework.util.LinkedMultiValueMap
import org.springframework.web.client.RestTemplate
import org.vd.vlogdeputatarb.util.util.properties.AppProperties
import org.vd.vlogdeputatarb.util.util.properties.TurnstileProperties
import java.time.Duration
import kotlin.collections.get

@Service
class CaptchaService(
    private val turnstile: TurnstileProperties,
    private val restTemplate: RestTemplate,
    private val app: AppProperties
){
    private val TURNSTILE_CAPTCHA_URL = "https://challenges.cloudflare.com/turnstile/v0/siteverify"

    private val registerLimit= bucket(2, Duration.ofDays(1))
    fun captchaRequiredForRegister(ip: String): Boolean =
        !registerLimit.tryConsume(ip)




    fun verify(token: String, expectedAction: String, ip: String): Boolean {

        val body = LinkedMultiValueMap<String, String>().apply {
            add("secret", turnstile.secret)
            add("response", token)
            add("remoteip", ip)   // ВАЖНО
        }

        val headers = HttpHeaders().apply {
            contentType = MediaType.APPLICATION_FORM_URLENCODED
        }

        val response = restTemplate.postForObject(
            TURNSTILE_CAPTCHA_URL,
            HttpEntity(body, headers),
            Map::class.java
        ) ?: return false

        val success = response["success"] == true

        val hostname = response["hostname"]

        return success  &&
                hostname == app.hostname
    }

    private fun bucket(limit: Long, refill: Duration) =
        object {
            private val cache = Caffeine.newBuilder()
                .expireAfterAccess(refill)
                .maximumSize(100_000)
                .build<String, Bucket>()

            fun tryConsume(key: String): Boolean {
                val bucket = cache.get(key) {
                    Bucket.builder()
                        .addLimit(
                            Bandwidth.classic(
                                limit,
                                Refill.intervally(limit, refill)
                            )
                        ).build()
                }
                return bucket.tryConsume(1)
            }
        }
}