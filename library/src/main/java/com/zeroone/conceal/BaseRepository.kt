package com.zeroone.conceal

import android.content.Context
import android.content.SharedPreferences

import com.zeroone.conceal.listener.OnDataChamberChangeListener

/**
 * Created by hafiq on 28/01/2018.
 */

open class BaseRepository {

    var mContext: Context? = null

    val chamber: SharedPreferences
        get() = sharedPreferences!!

    val chamberEditor: SharedPreferences.Editor?
        get() = editor

    fun throwRunTimeException(message: String, throwable: Throwable) {
        RuntimeException(message, throwable).printStackTrace()
    }

    fun hashKey(key: String): String {
        return secretChamber!!.hashVault(defaultPrefix!! + key)
    }

    fun hideValue(value: String): String? {
        return secretChamber!!.lockVault(value)
    }

    fun getmContext(): Context? {
        return mContext
    }

    fun getSecretChamber(): SecretChamber? {
        return secretChamber
    }

    fun getOnDataChangeListener(): OnDataChamberChangeListener? {
        return onDataChangeListener
    }

    fun getDefaultPrefix(): String? {
        return defaultPrefix
    }

    fun getChamberFolderName(): String {
        return chamberFolderName
    }

    companion object {
        var chamberFolderName: String = ""
        var sharedPreferences: SharedPreferences? = null
        var editor: SharedPreferences.Editor? = null
        var secretChamber: SecretChamber? = null
        var onDataChangeListener: OnDataChamberChangeListener? = null
        var defaultPrefix: String? = null
    }
}
