package com.zeroone.conceal

import android.content.SharedPreferences

import java.lang.reflect.Type

/**
 * Created by hafiq on 28/01/2018.
 */

abstract class DeviceAbstract<T : DeviceAbstract<T>> : BaseBuilderAbstract {
    protected abstract val isDeviceUpdate: Boolean?
    protected abstract val deviceId: String
    protected abstract val deviceVersion: String
    protected abstract val deviceOs: String

    constructor(sharedPreferences: SharedPreferences?) : super(sharedPreferences) {}

    constructor(keyPrefix: String?, sharedPreferences: SharedPreferences?) : super(keyPrefix, sharedPreferences) {}

    constructor(keyPrefix: String?, defaultEmptyValue: String?, sharedPreferences: SharedPreferences?) : super(keyPrefix, defaultEmptyValue, sharedPreferences) {}

    protected abstract fun setDefault(defaultEmptyValue: String?): T
    protected abstract fun setDeviceId(deviceId: String): T
    protected abstract fun applyDeviceId(deviceId: String)
    protected abstract fun setDeviceVersion(version: String): T
    protected abstract fun applyDeviceVersion(version: String)
    protected abstract fun setDeviceIsUpdated(updated: Boolean): T
    protected abstract fun applyDeviceIsUpdated(updated: Boolean)
    protected abstract fun setDeviceOS(os: String): T
    protected abstract fun applyDeviceOS(os: String)
    protected abstract fun setDeviceDetail(`object`: Any): T
    protected abstract fun applyDeviceDetail(`object`: Any)
    protected abstract fun getDeviceDetail(typeOfT: Type): Any
    protected abstract fun getDeviceDetail(classOfT: Class<Any>): Any

}
