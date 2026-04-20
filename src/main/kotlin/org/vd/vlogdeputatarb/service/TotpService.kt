package org.vd.vlogdeputatarb.service

import com.google.zxing.BarcodeFormat
import com.google.zxing.client.j2se.MatrixToImageWriter
import com.google.zxing.qrcode.QRCodeWriter
import com.warrenstrange.googleauth.GoogleAuthenticator
import com.warrenstrange.googleauth.GoogleAuthenticatorKey
import org.springframework.stereotype.Service
import java.io.ByteArrayOutputStream
import java.net.URLEncoder

@Service
class TotpService {

    private val gAuth = GoogleAuthenticator()

    /**
     * Создание credentials (секрет + параметры)
     */
    fun createCredentials(): GoogleAuthenticatorKey {
        return gAuth.createCredentials()
    }

    /**
     * Проверка кода
     */
    fun verifyCode(secret: String, code: String): Boolean {
        return try {
            gAuth.authorize(secret, code.toInt())
        } catch (e: Exception) {
            false
        }
    }

    fun buildOtpAuthUrl(
        username: String,
        secret: String
    ): String {

        val issuer = "VlogDeputata"
        val encodedIssuer = URLEncoder.encode(issuer, Charsets.UTF_8)
        val encodedUsername = URLEncoder.encode(username, Charsets.UTF_8)

        return "otpauth://totp/$encodedIssuer:$encodedUsername" +
                "?secret=$secret&issuer=$encodedIssuer"
    }

    /**
     * Генерация QR (лучше 350px)
     */
    fun generateQrCodeImage(text: String): ByteArray {
        val bitMatrix = QRCodeWriter()
            .encode(text, BarcodeFormat.QR_CODE, 350, 350)

        val stream = ByteArrayOutputStream()
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", stream)
        return stream.toByteArray()
    }
}