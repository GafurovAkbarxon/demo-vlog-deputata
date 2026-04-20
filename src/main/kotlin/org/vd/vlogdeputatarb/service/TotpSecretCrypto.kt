package org.vd.vlogdeputatarb.service

import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import org.vd.vlogdeputatarb.util.util.properties.AppProperties
import org.vd.vlogdeputatarb.util.util.properties.TotpProperties
import java.security.SecureRandom
import java.util.Base64
import javax.crypto.Cipher
import javax.crypto.spec.GCMParameterSpec
import javax.crypto.spec.SecretKeySpec
//Криптография: AES/GCM/NoPadding
@Service
class TotpSecretCrypto(
    private val totp: TotpProperties
) {

    private val key = SecretKeySpec(
        Base64.getDecoder().decode(totp.encKey),
        "AES"
    )

    fun encrypt(secret: String): String {
        val iv = ByteArray(12)
        SecureRandom().nextBytes(iv)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.ENCRYPT_MODE, key, GCMParameterSpec(128, iv))

        val encrypted = cipher.doFinal(secret.toByteArray())
        val result = iv + encrypted

        return Base64.getEncoder().encodeToString(result)
    }

    fun decrypt(data: String): String {
        val bytes = Base64.getDecoder().decode(data)

        val iv = bytes.copyOfRange(0, 12)
        val encrypted = bytes.copyOfRange(12, bytes.size)

        val cipher = Cipher.getInstance("AES/GCM/NoPadding")
        cipher.init(Cipher.DECRYPT_MODE, key, GCMParameterSpec(128, iv))

        return String(cipher.doFinal(encrypted))
    }
}