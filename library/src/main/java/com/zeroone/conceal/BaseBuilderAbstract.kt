package com.zeroone.conceal

import android.annotation.SuppressLint
import android.content.SharedPreferences

/**
 * Created by hafiq on 28/01/2018.
 */

@SuppressLint("CommitPrefEdits")
abstract class BaseBuilderAbstract {

    private var defaultPrefix: String? = ""
    private var DEFAULT_VALUE: String? = null
    private val SEPARATOR = "_"
    private var innerSharedPreferences: SharedPreferences? = null
    var editor: SharedPreferences.Editor? = null
        get() {
            if (field == null) {
                throw IllegalArgumentException("Need to initialize SharedChamber.ChamberBuilder first")
            }

            return field
        }
    var secretChamber: SecretChamber? = null

    constructor(sharedPreferences: SharedPreferences?) {
        this.defaultPrefix = null

        if (sharedPreferences == null) {
            throw IllegalArgumentException("Need to initialize SharedChamber.ChamberBuilder first")
        }

        this.innerSharedPreferences = sharedPreferences
        this.editor = this.innerSharedPreferences!!.edit()
    }

    constructor(keyPrefix: String?, sharedPreferences: SharedPreferences?) {
        if (keyPrefix != null)
            this.defaultPrefix = keyPrefix + this.SEPARATOR

        if (sharedPreferences == null) {
            throw IllegalArgumentException("Need to initialize SharedChamber.ChamberBuilder first")
        }

        this.innerSharedPreferences = sharedPreferences
        this.editor = this.innerSharedPreferences!!.edit()

    }

    constructor(keyPrefix: String?, defaultEmptyValue: String?, sharedPreferences: SharedPreferences?) {
        if (defaultEmptyValue != null) {
            this.DEFAULT_VALUE = defaultEmptyValue
        }

        if (keyPrefix != null)
            this.defaultPrefix = keyPrefix + this.SEPARATOR

        if (sharedPreferences == null) {
            throw IllegalArgumentException("Need to initialize SharedChamber.ChamberBuilder first")
        }

        this.innerSharedPreferences = sharedPreferences
        this.editor = this.innerSharedPreferences!!.edit()

    }

    fun setDefaultPrefix(defaultPrefix: String?) {
        this.defaultPrefix = defaultPrefix
    }

    fun setDefaultValue(defaultValue: String?) {
        if (defaultValue != null) {
            this.DEFAULT_VALUE = defaultValue
        }
    }

    fun returnValue(KEY: String): String? {

        return secretChamber!!.openVault(innerSharedPreferences!!.getString(setHashKey(KEY), null))
                ?: return DEFAULT_VALUE
    }

    fun setHashKey(key: String): String {
        return this.secretChamber!!.hashVault(defaultPrefix + key)
    }

    fun hideValue(value: String): String? {
        return this.secretChamber!!.lockVault(value)
    }

    fun apply() {
        editor!!.apply()
    }

    fun commit() {
        editor!!.commit()
    }
}
