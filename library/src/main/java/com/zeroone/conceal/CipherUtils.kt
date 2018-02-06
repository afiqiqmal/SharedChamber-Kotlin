package com.zeroone.conceal

import android.util.Base64
import com.zeroone.conceal.model.Constant.UTF8
import java.io.UnsupportedEncodingException
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

/**
 * @author : hafiq on 29/03/2017.
 */

internal object CipherUtils {

    @JvmStatic
    fun obscureEncodeSixFourString(plaintext: ByteArray): String {
        return Base64.encodeToString(plaintext, Base64.DEFAULT)
    }

    @JvmStatic
    fun obscureEncodeSixFourBytes(plaintext: ByteArray): ByteArray {
        return Base64.encode(plaintext, Base64.DEFAULT)
    }

    @JvmStatic
    fun deObscureSixFour(cipher: String): ByteArray {
        return Base64.decode(cipher, Base64.DEFAULT)
    }

    @JvmStatic
    fun deObscureSixFour(cipher: ByteArray): ByteArray {
        return Base64.decode(cipher, Base64.DEFAULT)
    }

    @JvmStatic
    fun encode64WithIteration(plaintext: String, iteration: Int): String {
        try {
            var dataDec = plaintext.toByteArray(charset(UTF8))
            for (x in 0 until iteration) {
                dataDec = obscureEncodeSixFourBytes(dataDec)
            }

            return String(dataDec)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return plaintext
    }

    @JvmStatic
    fun decode64WithIteration(plaintext: String, iteration: Int): String? {
        try {
            var dataDec = plaintext.toByteArray(charset(UTF8))
            for (x in 0 until iteration) {
                dataDec = deObscureSixFour(dataDec)
            }

            return String(dataDec)
        } catch (e: UnsupportedEncodingException) {
            e.printStackTrace()
        }

        return null
    }

    @JvmStatic
    fun getRandomString(length: Int): String {
        val chars = "abcdefghijklmnopqrstuvwxyz".toCharArray()
        val sb = StringBuilder()
        val random = Random()
        for (i in 0 until length) {
            val c = chars[random.nextInt(chars.size)]
            sb.append(c)
        }

        return sb.toString()
    }

    @JvmStatic
    fun aesCrypt(key: String, iv: String, data: String): String? {
        var key = key
        try {
            val CIPHER_KEY_LEN = 16
            if (key.length < CIPHER_KEY_LEN) {
                val numPad = CIPHER_KEY_LEN - key.length

                val keyBuilder = StringBuilder(key)
                for (i in 0 until numPad) {
                    keyBuilder.append("0")
                }
                key = keyBuilder.toString()

            } else if (key.length > CIPHER_KEY_LEN) {
                key = key.substring(0, CIPHER_KEY_LEN) //truncate to 16 bytes
            }


            val initVector = IvParameterSpec(iv.toByteArray(charset(UTF8)))
            val skeySpec = SecretKeySpec(key.toByteArray(charset(UTF8)), "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            cipher.init(Cipher.ENCRYPT_MODE, skeySpec, initVector)

            val encryptedData = cipher.doFinal(data.toByteArray())

            val base64_EncryptedData = Base64.encodeToString(encryptedData, Base64.DEFAULT)
            val base64_IV = Base64.encodeToString(iv.toByteArray(charset(UTF8)), Base64.DEFAULT)

            return Base64.encodeToString((base64_EncryptedData + ":" + base64_IV).toByteArray(charset(UTF8)), Base64.DEFAULT)

        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return null
    }

    @JvmStatic
    fun aesDecrypt(key: String, data: String): String? {
        var key = key
        try {

            val CIPHER_KEY_LEN = 16
            if (key.length < CIPHER_KEY_LEN) {
                val numPad = CIPHER_KEY_LEN - key.length

                val keyBuilder = StringBuilder(key)
                for (i in 0 until numPad) {
                    keyBuilder.append("0")
                }
                key = keyBuilder.toString()

            } else if (key.length > CIPHER_KEY_LEN) {
                key = key.substring(0, CIPHER_KEY_LEN) //truncate to 16 bytes
            }

            val parts = String(Base64.decode(data, Base64.DEFAULT)).split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()

            val iv = IvParameterSpec(Base64.decode(parts[1], Base64.DEFAULT))
            val skeySpec = SecretKeySpec(key.toByteArray(charset(UTF8)), "AES")

            val cipher = Cipher.getInstance("AES/CBC/PKCS5PADDING")
            cipher.init(Cipher.DECRYPT_MODE, skeySpec, iv)

            val decodedEncryptedData = Base64.decode(parts[0], Base64.DEFAULT)

            val original = cipher.doFinal(decodedEncryptedData)

            return String(original)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }

        return null
    }
}
