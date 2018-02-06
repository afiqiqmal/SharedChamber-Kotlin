package com.chamber.kotlin.library

import android.content.Context
import android.content.SharedPreferences

import com.zeroone.conceal.listener.OnDataChamberChangeListener
import com.chamber.kotlin.library.model.ChamberType

/**
* @author by hafiq on 28/01/2018.
*/

abstract class BasePreferencesBuilder<T : BasePreferencesBuilder<T>>(var context: Context?) {
    var keyChain = ChamberType.KEY_256
    var prefName: String? = null
    private var mFolderName: String? = null
    var defaultPrefix = ""
    var isEnabledCrypto = false
    var isEnableCryptKey = false
    var entityPasswordRaw: String? = null
    var sharedPreferences: SharedPreferences? = null
    var onDataChangeListener: OnDataChamberChangeListener? = null

    fun getFolderName(): String? {
        return mFolderName
    }

    fun setmFolderName(mFolderName: String) {
        this.mFolderName = mFolderName
    }

    protected abstract fun useThisPrefStorage(mPrefname: String): T
    protected abstract fun enableCrypto(encryptKey: Boolean, encryptValue: Boolean): T
    protected abstract fun enableKeyPrefix(enable: Boolean, defaultPrefix: String?): T
    protected abstract fun setPrefListener(listener: OnDataChamberChangeListener): T
    protected abstract fun setFolderName(folderName: String): T
    protected abstract fun setPassword(password: String): T
    protected abstract fun setChamberType(keyChain: ChamberType): T

}
