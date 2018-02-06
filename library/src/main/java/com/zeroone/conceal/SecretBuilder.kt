package com.zeroone.conceal

import android.content.Context

import com.facebook.android.crypto.keychain.AndroidConceal
import com.facebook.android.crypto.keychain.SharedPrefsBackedKeyChain
import com.facebook.crypto.Crypto
import com.facebook.crypto.Entity
import com.facebook.crypto.keychain.KeyChain
import com.zeroone.conceal.model.ChamberType

import java.lang.ref.WeakReference

/**
 * Created by hafiq on 05/02/2018.
 */

class SecretBuilder(context: Context) {
    private val context: WeakReference<Context>?

    var makeKeyChain: KeyChain? = null
        private set
    var crypto: Crypto? = null
        private set
    var defaultKeyChain: ChamberType? = ChamberType.KEY_256
        private set
    var isEnableCrypto = true
        private set
    var isEnableHashKey = true
        private set
    var entityPassword: Entity? = null
        private set
    var entityPasswordRaw = BuildConfig.APPLICATION_ID
        private set
    var folderName: String? = null
        private set

    init {
        this.context = WeakReference(context.applicationContext)
    }

    fun setChamberType(config: ChamberType): SecretBuilder {
        this.defaultKeyChain = config
        return this
    }

    fun setEnableValueEncryption(enableCrypto: Boolean): SecretBuilder {
        this.isEnableCrypto = enableCrypto
        return this
    }

    fun setEnableKeyEncryption(enableKeyCrypt: Boolean): SecretBuilder {
        this.isEnableHashKey = enableKeyCrypt
        return this
    }

    fun setPassword(password: String?): SecretBuilder {
        if (password != null) entityPasswordRaw = password
        return this
    }

    /***
     * @param folderName - Main Folder to be stored
     * @return - SecretBuilder
     */
    fun setStoredFolder(folderName: String): SecretBuilder {
        this.folderName = folderName
        return this
    }

    fun buildSecret(): SecretChamber {

        if (this.context == null) {
            throw RuntimeException("Context cannot be null")
        }

        entityPassword = Entity.create(CipherUtils.obscureEncodeSixFourString(entityPasswordRaw.toByteArray()))
        makeKeyChain = SharedPrefsBackedKeyChain(this.context.get()!!, if (defaultKeyChain == null) ChamberType.KEY_256.config else defaultKeyChain!!.config)

        crypto = when (defaultKeyChain) {
            null -> AndroidConceal.get().createDefaultCrypto(makeKeyChain)
            ChamberType.KEY_128 -> AndroidConceal.get().createCrypto128Bits(makeKeyChain)
            else -> AndroidConceal.get().createCrypto256Bits(makeKeyChain)
        }

        return SecretChamber(this)
    }
}
