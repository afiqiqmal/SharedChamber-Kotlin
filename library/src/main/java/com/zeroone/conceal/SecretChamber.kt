package com.zeroone.conceal

import android.Manifest
import android.content.Context
import android.support.annotation.RequiresPermission
import com.facebook.android.crypto.keychain.AndroidConceal
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain
import com.facebook.crypto.Crypto
import com.facebook.crypto.Entity
import com.facebook.crypto.exception.CryptoInitializationException
import com.facebook.crypto.exception.KeyChainException
import com.facebook.crypto.keychain.KeyChain
import com.zeroone.conceal.model.ChamberType
import com.zeroone.conceal.model.Constant.DEFAULT_DIRECTORY
import com.zeroone.conceal.model.Constant.DEFAULT_FILES_FOLDER
import com.zeroone.conceal.model.Constant.DEFAULT_IMAGE_FOLDER
import com.zeroone.conceal.model.Constant.DEFAULT_MAIN_FOLDER
import com.zeroone.conceal.model.Constant.DEFAULT_PREFIX_FILENAME
import java.io.*
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException

/**
 * @author : hafiq on 23/03/2017.
 */
class SecretChamber {

    var crypto: Crypto? = null

    private var keyChain: KeyChain? = null
    private var mEntityPassword = Entity.create(BuildConfig.APPLICATION_ID)
    private var mEntityPasswordRaw: String? = null
    private var enableCrypto = true
    private var enableHashKey = true
    private var mainDirectoryName: String? = null

    constructor(builder: SecretBuilder) {
        crypto = builder.crypto
        mEntityPassword = builder.entityPassword
        mEntityPasswordRaw = builder.entityPasswordRaw
        enableCrypto = builder.isEnableCrypto
        enableHashKey = builder.isEnableHashKey
        mainDirectoryName = builder.folderName

        if (mainDirectoryName == null) mainDirectoryName = DEFAULT_MAIN_FOLDER
    }

    constructor(context: Context, config: ChamberType?) {
        keyChain = SharedPrefsBackedKeyChain(context, if (config == null) ChamberType.KEY_256.config else config.config)
        crypto = AndroidConceal.get().createDefaultCrypto(keyChain)
    }

    fun setEntityPassword(mEntityPassword: Entity?) {
        if (mEntityPassword != null) this.mEntityPassword = mEntityPassword
    }

    fun setEntityPassword(password: String?) {
        if (password != null) this.mEntityPassword = Entity.create(CipherUtils.obscureEncodeSixFourString(password.toByteArray()))
    }

    fun setEnableCrypto(enableCrypto: Boolean) {
        this.enableCrypto = enableCrypto
    }

    fun setEnableKeyCrypto(enableKeyCrypt: Boolean) {
        this.enableHashKey = enableKeyCrypt
    }

    private fun makeDirectory(): String {
        if (mainDirectoryName == null) mainDirectoryName = DEFAULT_MAIN_FOLDER

        return DEFAULT_DIRECTORY + mainDirectoryName + "/"
    }

    private fun makeFileDirectory(): String {
        return makeDirectory() + DEFAULT_FILES_FOLDER
    }

    private fun makeImagesDirectory(): String {
        return makeDirectory() + DEFAULT_IMAGE_FOLDER
    }

    fun clearCrypto() {
        if (crypto!!.isAvailable) {
            keyChain!!.destroyKeys()
        }
    }


    /***
     * Encryption of plaintext
     * @param plain - plaintext of string to be encrypt
     * @return String
     */
    fun lockVault(plain: String?): String? {
        if (plain == null)
            return null

        return if (enableCrypto) {
            try {
                val a = crypto!!.encrypt(plain.toByteArray(), mEntityPassword)
                CipherUtils.obscureEncodeSixFourString(a)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

        } else {
            plain
        }
    }

    /***
     * Encryption of bytes
     * @param bytes - array bytes to be encrypt
     * @return bytes
     */
    fun lockVault(bytes: ByteArray?): ByteArray? {
        if (bytes == null)
            return null

        return if (enableCrypto) {
            try {
                val a = crypto!!.encrypt(bytes, mEntityPassword)
                CipherUtils.obscureEncodeSixFourBytes(a)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

        } else {
            bytes
        }
    }

    //encrypt files
    @RequiresPermission(allOf = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    fun lockVaultFile(file: File, deleteOldFile: Boolean): File? {
        return if (enableCrypto) {
            try {
                val isImage = FileUtils.isFileForImage(file)

                val mEncryptedFile = File(makeDirectory() + DEFAULT_PREFIX_FILENAME + file.name)
                val fileStream = BufferedOutputStream(FileOutputStream(mEncryptedFile))
                val outputStream = crypto!!.getMacOutputStream(fileStream, mEntityPassword)

                var read: Int
                val buffer = ByteArray(1024)
                val bis = BufferedInputStream(FileInputStream(file))
                do  {
                    read = bis.read(buffer);
                    outputStream.write(buffer, 0, read)
                }while (read != -1)
                outputStream.close()
                bis.close()

                if (deleteOldFile)
                    file.delete()

                val pathDir = File(if (isImage) makeImagesDirectory() else makeFileDirectory())
                FileUtils.moveFile(mEncryptedFile, pathDir)

            } catch (e: KeyChainException) {
                e.printStackTrace()
                null
            } catch (e: CryptoInitializationException) {
                e.printStackTrace()
                null
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }

        } else {
            file
        }
    }

    /**
     * Decryption string
     * @param cipher cipher string
     * @return String plaintext
     */
    fun openVault(cipher: String?): String? {
        if (cipher == null)
            return null

        return if (enableCrypto) {
            try {
                String(crypto!!.decrypt(CipherUtils.deObscureSixFour(cipher), mEntityPassword))
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

        } else {
            cipher
        }
    }

    /**
     * Decryption bytes
     * @param cipher cipher bytes[]
     * @return String plaintext
     */
    fun openVault(cipher: ByteArray?): ByteArray? {
        if (cipher == null)
            return null

        return if (enableCrypto) {
            try {
                crypto!!.decrypt(CipherUtils.deObscureSixFour(cipher), mEntityPassword)
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }

        } else {
            cipher
        }
    }

    //decrypt file
    @RequiresPermission(allOf = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    fun openVaultFile(file: File, deleteOldFile: Boolean): File? {
        if (enableCrypto) {
            try {
                if (file.name.contains(DEFAULT_PREFIX_FILENAME)) {

                    val isImage = FileUtils.isFileForImage(file)

                    val mDecryptedFile = File(makeDirectory() + file.name.replace(DEFAULT_PREFIX_FILENAME, ""))

                    val inputStream = crypto!!.getMacInputStream(FileInputStream(file), mEntityPassword)
                    val out = ByteArrayOutputStream()

                    val outputStream = FileOutputStream(mDecryptedFile)
                    val bis = BufferedInputStream(inputStream)
                    var mRead: Int
                    val mBuffer = ByteArray(1024)
                    do {
                        mRead = bis.read(mBuffer);
                        outputStream.write(mBuffer, 0, mRead)
                    } while (mRead != -1)
                    bis.close()
                    out.writeTo(outputStream)
                    inputStream.close()
                    outputStream.close()
                    out.close()

                    if (deleteOldFile)
                        file.delete()

                    val pathDir = File(if (isImage) makeImagesDirectory() else makeFileDirectory())
                    return FileUtils.moveFile(mDecryptedFile, pathDir)
                }

                return null

            } catch (e: KeyChainException) {
                e.printStackTrace()
                return null
            } catch (e: CryptoInitializationException) {
                e.printStackTrace()
                return null
            } catch (e: IOException) {
                e.printStackTrace()
                return null
            }

        }

        return file
    }

    /***
     * hashing sharedpref key
     * @param key - key
     * @return - hash key
     */
    fun hashVault(key: String): String {
        if (enableHashKey) {
            try {
                val HEX_CHARS = "0123456789ABCDEF"
                val md = MessageDigest.getInstance("SHA-256").digest(key.toByteArray());
                val result = StringBuilder(md.size * 2)

                md.forEach {
                    val i = it.toInt()
                    result.append(HEX_CHARS[i shr 4 and 0x0f])
                    result.append(HEX_CHARS[i and 0x0f])
                }
                return result.toString()

            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }

        }

        return key
    }


    /***
     * encrypt base64 with iteration Conceal
     */

    fun lockVaultBase(plainText: String, iteration: Int): String? {
        var cipher: String? = plainText
        for (x in 0 until iteration) {
            cipher = lockVault(cipher)
        }

        return cipher
    }

    fun openVaultBase(cipher: String, iteration: Int): String? {
        var plainText: String? = cipher
        for (x in 0 until iteration) {
            plainText = openVault(plainText)
        }

        return plainText
    }

    fun lockVaultAes(plainText: String): String? {
        return CipherUtils.aesCrypt(mEntityPasswordRaw!!, CipherUtils.getRandomString(16), plainText)
    }

    fun openVaultAes(plainText: String): String? {
        return CipherUtils.aesDecrypt(mEntityPasswordRaw!!, plainText)
    }

}
