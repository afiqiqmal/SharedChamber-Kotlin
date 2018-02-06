package com.chamber.kotlin.library

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.preference.PreferenceManager
import android.support.annotation.CheckResult
import android.support.annotation.DrawableRes
import android.support.annotation.RequiresPermission
import com.facebook.soloader.SoLoader
import com.google.gson.Gson
import com.chamber.kotlin.library.FileUtils.getDirectory
import com.chamber.kotlin.library.FileUtils.getImageDirectory
import com.chamber.kotlin.library.FileUtils.getListFiles
import com.zeroone.conceal.listener.OnDataChamberChangeListener
import com.chamber.kotlin.library.model.ChamberType
import com.chamber.kotlin.library.model.Constant
import com.chamber.kotlin.library.model.Constant.ADDRESS
import com.chamber.kotlin.library.model.Constant.AGE
import com.chamber.kotlin.library.model.Constant.BIRTH_DATE
import com.chamber.kotlin.library.model.Constant.DEFAULT_MAIN_FOLDER
import com.chamber.kotlin.library.model.Constant.DEVICE_DETAIL
import com.chamber.kotlin.library.model.Constant.DEVICE_ID
import com.chamber.kotlin.library.model.Constant.DEVICE_IS_UPDATE
import com.chamber.kotlin.library.model.Constant.DEVICE_OS
import com.chamber.kotlin.library.model.Constant.DEVICE_VERSION
import com.chamber.kotlin.library.model.Constant.EMAIL
import com.chamber.kotlin.library.model.Constant.FIRST_NAME
import com.chamber.kotlin.library.model.Constant.FIRST_TIME_USER
import com.chamber.kotlin.library.model.Constant.FULLNAME
import com.chamber.kotlin.library.model.Constant.GENDER
import com.chamber.kotlin.library.model.Constant.HAS_LOGIN
import com.chamber.kotlin.library.model.Constant.LAST_NAME
import com.chamber.kotlin.library.model.Constant.MOBILE_NO
import com.chamber.kotlin.library.model.Constant.NAME
import com.chamber.kotlin.library.model.Constant.PASSWORD
import com.chamber.kotlin.library.model.Constant.PHONE_NO
import com.chamber.kotlin.library.model.Constant.PUSH_TOKEN
import com.chamber.kotlin.library.model.Constant.USER_ID
import com.chamber.kotlin.library.model.Constant.USER_JSON
import com.chamber.kotlin.library.model.CryptoFile
import java.io.File
import java.io.IOException
import java.lang.reflect.Type
import java.util.*

/**
 * @author : hafiq on 23/03/2017.
 */
@SuppressLint("CommitPrefEdits")
class SharedChamber private constructor(builder: ChamberBuilder) : BaseRepository() {

    init {
        mContext = builder.context
        chamberFolderName = builder.getFolderName()!!
        sharedPreferences = builder.sharedPreferences
        onDataChangeListener = builder.onDataChangeListener
        defaultPrefix = builder.defaultPrefix

        val mKeyChain = builder.keyChain
        val mEnabledCrypto = builder.isEnabledCrypto
        val mEnableCryptKey = builder.isEnableCryptKey
        val mEntityPasswordRaw = builder.entityPasswordRaw

        //init editor
        editor = sharedPreferences!!.edit()

        //init crypto
        secretChamber = SecretBuilder(mContext!!)
                .setPassword(mEntityPasswordRaw)
                .setChamberType(mKeyChain)
                .setEnableValueEncryption(mEnabledCrypto)
                .setEnableKeyEncryption(mEnableCryptKey)
                .setStoredFolder(getChamberFolderName())
                .buildSecret()

        //init listener if set
        if (onDataChangeListener != null) {
            sharedPreferences!!.
                    registerOnSharedPreferenceChangeListener({
                        sharedPreferences, key -> onDataChangeListener!!.onDataChange(key, sharedPreferences.getString(key, ""))
                    })
        }
    }

    /*******************************
     * GET SHAREDPREFERENCES TOTAL
     */
    val chamberSize: Int
        get() = chamber.all.size

    /**
     * get all encrypted file in created folder
     * @return @CryptoFile
     */
    val allChamberFiles: List<CryptoFile>
        get() = getListFiles(getDirectory(getChamberFolderName()))

    /**
     * get list of key and values inside sharedPreferences
     * @return Map
     */
    val everythingInChamberInMap: Map<String, String>
        get() {
            val keys = chamber.all
            val data = HashMap<String, String>()

            for ((key, value) in keys) {
                try {
                    data[key] = value.toString()
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            return data
        }

    val everythingInChamberInList: List<String>
        get() {
            val keys = chamber.all
            val data = ArrayList<String>()

            for ((key, value) in keys) {
                try {
                    data.add("[" + key + "] : " + value.toString())
                } catch (e: Exception) {
                    e.printStackTrace()
                }

            }

            return data
        }

    /**********************
     * DESTROY FILES
     */
    fun destroyChamber() {
        getSecretChamber()!!.clearCrypto()
    }

    fun clearChamber() {
        try {
            chamberEditor.clear().apply()
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }


    /*******************************
     * REMOVING KEYS
     */
    /* Remove by Key */
    fun remove(vararg keys: String) {
        for (key in keys) {
            chamberEditor.remove(hashKey(key))
        }
        chamberEditor.apply()
    }

    fun remove(key: String) {
        chamberEditor.remove(hashKey(key)).apply()
    }

    /**
     * special cases for file to remove by key
     * @param key preferences key
     * @return boolean
     */
    fun removeFile(key: String): Boolean {
        val path = getString(key)
        if (path != null) {
            val imagePath = File(path)
            if (imagePath.exists()) {
                if (!imagePath.delete()) {
                    return false
                }
                remove(key)
            }
            return true
        }
        return false
    }

    /**
     * check whether value is existed or not
     * @param key - key string
     * @return - value
     */
    operator fun contains(key: String): Boolean {
        return chamber.contains(hashKey(key))
    }

    /* Save Data */

    fun put(key: String, value: String) {
        chamberEditor.putString(hashKey(key), hideValue(value)).apply()
    }

    fun put(key: String, value: Int) {
        put(key, Integer.toString(value))
    }

    fun put(key: String, value: Long) {
        put(key, java.lang.Long.toString(value))
    }

    fun put(key: String, value: Double) {
        put(key, java.lang.Double.toString(value))
    }

    fun put(key: String, value: Float) {
        put(key, java.lang.Float.toString(value))
    }

    fun put(key: String, value: Boolean) {
        put(key, java.lang.Boolean.toString(value))
    }

    fun put(key: String, value: List<*>) {
        put(key, value.toString())
    }

    fun put(key: String, values: Map<String, String>) {
        put(key, ConverterListUtils.convertMapToString(values))
    }

    fun put(key: String, bytes: ByteArray) {
        put(key, String(bytes))
    }

    @RequiresPermission(allOf = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    fun put(key: String, bitmap: Bitmap): String? {
        val imageFile = File(getImageDirectory(getChamberFolderName()), "images_" + System.currentTimeMillis() + ".png")
        if (FileUtils.saveBitmap(imageFile, bitmap)) {
            getSecretChamber()!!.lockVaultFile(imageFile, true)
            put(key, imageFile.absolutePath)
            return imageFile.absolutePath
        }
        return null
    }

    @RequiresPermission(allOf = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    fun put(key: String, file: File?): String? {
        if (FileUtils.isFileForImage(file)) {
            val imageFile = FileUtils.moveFile(file, getImageDirectory(getChamberFolderName()))
            if (imageFile != null && imageFile.exists()) {
                getSecretChamber()!!.lockVaultFile(imageFile, true)
                put(key, imageFile.absolutePath)
                return imageFile.absolutePath
            }
        }
        return null
    }

    @RequiresPermission(allOf = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    fun put(key: String, file: File?, deleteOldFile: Boolean): File? {

        if (file == null)
            return null

        try {
            if (file.exists() && !FileUtils.isFileForImage(file)) {
                val enc = getSecretChamber()!!.lockVaultFile(file, deleteOldFile)
                put(key, enc!!.absolutePath)
                return enc
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    @RequiresPermission(allOf = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    fun putDrawable(key: String, @DrawableRes resId: Int): String? {
        val bitmap = BitmapFactory.decodeResource(mContext!!.resources, resId)
        if (bitmap != null) {
            val imageFile = File(getImageDirectory(getChamberFolderName()), "images_" + System.currentTimeMillis() + ".png")
            if (FileUtils.saveBitmap(imageFile, bitmap)) {
                getSecretChamber()!!.lockVaultFile(imageFile, true)
                put(key, imageFile.absolutePath)
                return imageFile.absolutePath
            }
        } else {
            throw IllegalArgumentException(resId.toString() + " : Drawable not found!")
        }

        return null
    }

    // use for objects with GSON
    fun putModel(key: String, `object`: Any) {
        put(key, Gson().toJson(`object`))
    }


    /************************************
     * FETCHING DATA FROM SHAREDPREFS
     */
    @CheckResult
    fun getString(key: String): String? {
        return getSecretChamber()!!.openVault(chamber.getString(hashKey(key), null))
    }

    @CheckResult
    fun getString(key: String, defaultValue: String): String? {
        return getSecretChamber()!!.openVault(chamber.getString(hashKey(key), defaultValue))
    }

    @CheckResult
    fun getModel(key: String, typeOfT: Type): Any {
        val value = getString(key)
        return Gson().fromJson(value, typeOfT)
    }

    @CheckResult
    fun getModel(key: String, classOfT: Class<Any>): Any {
        val value = getString(key)
        return Gson().fromJson(value, classOfT)
    }

    @CheckResult
    fun getInt(key: String): Int? {
        try {
            val value = getString(key) ?: return -99

            return Integer.parseInt(value)
        } catch (e: Exception) {
            throwRunTimeException("Unable to convert to Integer data type", e)
            return -99
        }

    }

    @CheckResult
    fun getInt(key: String, defaultValue: Int): Int? {
        try {
            val value = getString(key) ?: return defaultValue

            return Integer.parseInt(value)
        } catch (e: Exception) {
            throwRunTimeException("Unable to convert to Integer data type", e)
            return -99
        }

    }

    @CheckResult
    fun getFloat(key: String): Float? {
        try {
            val value = getString(key) ?: return 0f

            return java.lang.Float.parseFloat(value)
        } catch (e: Exception) {
            throwRunTimeException("Unable to convert to Float data type", e)
            return 0f
        }

    }

    @CheckResult
    fun getFloat(key: String, defaultValue: Float): Float? {
        try {
            val value = getString(key) ?: return defaultValue

            return java.lang.Float.parseFloat(value)
        } catch (e: Exception) {
            throwRunTimeException("Unable to convert to Float data type", e)
            return defaultValue
        }

    }

    @CheckResult
    fun getDouble(key: String): Double? {
        try {
            val value = getString(key) ?: return 0.0
            return java.lang.Double.parseDouble(value)
        } catch (e: Exception) {
            throwRunTimeException("Unable to convert to Double data type", e)
            return 0.0
        }

    }

    @CheckResult
    fun getDouble(key: String, defaultValue: Double): Double? {
        try {
            val value = getString(key) ?: return defaultValue

            return java.lang.Double.parseDouble(value)
        } catch (e: Exception) {
            throwRunTimeException("Unable to convert to Double data type", e)
            return defaultValue
        }

    }

    @CheckResult
    fun getLong(key: String): Long? {
        try {
            val value = getString(key) ?: return 0L

            return java.lang.Long.parseLong(value)
        } catch (e: Exception) {
            throwRunTimeException("Unable to convert to Long data type", e)
            return 0L
        }

    }

    @CheckResult
    fun getLong(key: String, defaultValue: Long): Long? {
        try {
            val value = getString(key) ?: return defaultValue

            return java.lang.Long.parseLong(value)
        } catch (e: Exception) {
            throwRunTimeException("Unable to convert to Long data type", e)
            return defaultValue
        }

    }

    @CheckResult
    fun getBoolean(key: String): Boolean? {
        try {
            val value = getString(key)
            return value != null && java.lang.Boolean.parseBoolean(value)
        } catch (e: Exception) {
            throwRunTimeException("Unable to convert to Boolean data type", e)
            return false
        }

    }

    @CheckResult
    fun getBoolean(key: String, defaultValue: Boolean): Boolean? {
        try {
            val value = getString(key) ?: return defaultValue

            return java.lang.Boolean.parseBoolean(value)
        } catch (e: Exception) {
            throwRunTimeException("Unable to convert to Boolean data type", e)
            return false
        }

    }

    @CheckResult
    fun getListString(key: String): List<String> {
        return ConverterListUtils.toStringArray(getString(key)!!)
    }

    @CheckResult
    fun getListFloat(key: String): List<Float>? {
        return ConverterListUtils.toFloatArray(getString(key)!!)
    }

    @CheckResult
    fun getListDouble(key: String): List<Double>? {
        return ConverterListUtils.toDoubleArray(getString(key)!!)
    }

    @CheckResult
    fun getListBoolean(key: String): List<Boolean>? {
        return ConverterListUtils.toBooleanArray(getString(key)!!)
    }

    @CheckResult
    fun getListInteger(key: String): List<Int>? {
        return ConverterListUtils.toIntArray(getString(key)!!)
    }

    @CheckResult
    fun getListLong(key: String): List<Long>? {
        return ConverterListUtils.toLongArray(getString(key)!!)
    }

    @CheckResult
    fun getMap(key: String): LinkedHashMap<String, String> {
        return ConverterListUtils.convertStringToMap(getString(key)!!)
    }

    @CheckResult
    fun getArrayBytes(key: String): ByteArray {
        return getString(key)!!.toByteArray()
    }

    @RequiresPermission(allOf = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    @CheckResult
    fun getImage(key: String): Bitmap? {
        val path = getString(key)
        if (path != null) {
            try {
                val file = File(path)
                return BitmapFactory.decodeFile(getSecretChamber()!!.openVaultFile(file, true)!!.absolutePath)
            } catch (e: Exception) {
                return null
            }

        }
        return null
    }

    @RequiresPermission(allOf = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
    @CheckResult
    fun getFile(key: String, deleteOldFile: Boolean): File? {
        try {
            val path = getString(key) ?: return null

            val getFile = File(path)
            if (getFile.exists()) {

                return getSecretChamber()!!.openVaultFile(getFile, deleteOldFile)
                        ?: throw Exception("File can't decrypt")
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    class DeviceChamber : DeviceAbstract<DeviceChamber> {

        constructor() : super(sharedPreferences) {
            secretChamber = BaseRepository.secretChamber
        }

        constructor(keyPrefix: String?) : super(keyPrefix, sharedPreferences) {
            if (keyPrefix == null) {
                setDefaultPrefix(defaultPrefix)
            }
            secretChamber = BaseRepository.secretChamber
        }

        constructor(keyPrefix: String?, defaultEmptyValue: String?) : super(keyPrefix, defaultEmptyValue, sharedPreferences) {
            if (keyPrefix == null) {
                setDefaultPrefix(defaultPrefix!!)
            }
            secretChamber = BaseRepository.secretChamber
        }

        public override fun setDefault(defaultEmptyValue: String?): DeviceChamber {
            setDefaultValue(defaultEmptyValue)
            return this
        }

        public override val isDeviceUpdate: Boolean?
            get() {
                return try {
                    java.lang.Boolean.parseBoolean(returnValue(DEVICE_IS_UPDATE))
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }

            }

        public override val deviceId: String
            get() = returnValue(DEVICE_ID)!!
        public override val deviceVersion: String
            get() = returnValue(DEVICE_VERSION)!!
        public override val deviceOs: String
            get() = returnValue(DEVICE_OS)!!



        public override fun setDeviceId(deviceId: String): DeviceChamber {
            editor!!.putString(setHashKey(DEVICE_ID), hideValue(deviceId))
            return this
        }

        public override fun applyDeviceId(deviceId: String) {
            editor!!.putString(setHashKey(DEVICE_ID), hideValue(deviceId)).apply()
        }

        public override fun setDeviceVersion(version: String): DeviceChamber {
            editor!!.putString(setHashKey(DEVICE_VERSION), hideValue(version))
            return this
        }

        public override fun applyDeviceVersion(version: String) {
            editor!!.putString(setHashKey(DEVICE_VERSION), hideValue(version)).apply()
        }

        public override fun setDeviceIsUpdated(updated: Boolean): DeviceChamber {
            editor!!.putString(setHashKey(DEVICE_IS_UPDATE), hideValue(updated.toString()))
            return this
        }

        public override fun applyDeviceIsUpdated(updated: Boolean) {
            editor!!.putString(setHashKey(DEVICE_IS_UPDATE), hideValue(updated.toString())).apply()
        }

        public override fun setDeviceOS(os: String): DeviceChamber {
            editor!!.putString(setHashKey(DEVICE_OS), hideValue(os))
            return this
        }

        public override fun applyDeviceOS(os: String) {
            editor!!.putString(setHashKey(DEVICE_OS), hideValue(os)).apply()
        }

        public override fun setDeviceDetail(`object`: Any): DeviceChamber {
            editor!!.putString(setHashKey(DEVICE_DETAIL), hideValue(Gson().toJson(`object`)))
            return this
        }

        public override fun applyDeviceDetail(`object`: Any) {
            editor!!.putString(setHashKey(DEVICE_DETAIL), hideValue(Gson().toJson(`object`))).apply()
        }

        public override fun getDeviceDetail(typeOfT: Type): Any {
            val value = returnValue(DEVICE_DETAIL)
            return Gson().fromJson(value, typeOfT)
        }

        public override fun getDeviceDetail(classOfT: Class<Any>): Any {
            val value = returnValue(DEVICE_DETAIL)
            return Gson().fromJson(value, classOfT)
        }
    }

    class UserChamber : UserAbstract<UserChamber> {
        constructor() : super(sharedPreferences!!) {
            secretChamber = BaseRepository.secretChamber
        }

        constructor(keyPrefix: String?) : super(keyPrefix, sharedPreferences!!) {
            if (keyPrefix == null) {
                setDefaultPrefix(defaultPrefix!!)
            }
            secretChamber = BaseRepository.secretChamber
        }

        constructor(keyPrefix: String?, defaultEmptyValue: String?) : super(keyPrefix, defaultEmptyValue, sharedPreferences!!) {
            if (keyPrefix == null) {
                setDefaultPrefix(defaultPrefix!!)
            }
            secretChamber = BaseRepository.secretChamber
        }

        public override val userId: String
            @CheckResult
            get() = returnValue(USER_ID)!!

        public override val userDetail: String
            @CheckResult
            get() = returnValue(USER_JSON)!!

        public override val userName: String
            @CheckResult
            get() = returnValue(NAME)!!

        public override val fullName: String
            @CheckResult
            get() = returnValue(FULLNAME)!!

        public override val firstName: String
            @CheckResult
            get() = returnValue(FIRST_NAME)!!

        public override val lastName: String
            @CheckResult
            get() = returnValue(LAST_NAME)!!

        public override val age: Int?
            @CheckResult
            get() {
                return try {
                    Integer.parseInt(returnValue(AGE))
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }

            }

        public override val gender: String
            @CheckResult
            get() = returnValue(GENDER)!!

        public override val birthDate: String
            @CheckResult
            get() = returnValue(BIRTH_DATE)!!

        public override val address: String
            @CheckResult
            get() = returnValue(ADDRESS)!!

        public override val email: String
            @CheckResult
            get() = returnValue(EMAIL)!!

        public override val pushToken: String
            @CheckResult
            get() = returnValue(PUSH_TOKEN)!!

        public override val phoneNumber: String
            @CheckResult
            get() = returnValue(PHONE_NO)!!

        public override val mobileNumber: String
            @CheckResult
            get() = returnValue(MOBILE_NO)!!

        public override val password: String
            @CheckResult
            get() = returnValue(PASSWORD)!!

        public override val isFirstTimeUser: Boolean?
            @CheckResult
            get() {
                return try {
                    java.lang.Boolean.parseBoolean(returnValue(FIRST_TIME_USER))
                } catch (e: Exception) {
                    e.printStackTrace()
                    null
                }

            }

        public override fun setDefault(defaultEmptyValue: String?): UserChamber {
            setDefaultValue(defaultEmptyValue)
            return this
        }

        public override fun setUserDetail(user_detail: String): UserChamber {
            editor!!.putString(setHashKey(USER_JSON), hideValue(user_detail))
            return this
        }

        public override fun applyUserDetail(user_detail: String) {
            editor!!.putString(setHashKey(USER_JSON), hideValue(user_detail)).apply()
        }

        public override fun setUserId(user_id: String): UserChamber {
            editor!!.putString(setHashKey(USER_ID), hideValue(user_id))
            return this
        }

        public override fun applyUserId(user_id: String) {
            editor!!.putString(setHashKey(USER_ID), hideValue(user_id)).apply()
        }

        public override fun setUserName(name: String): UserChamber {
            editor!!.putString(setHashKey(NAME), hideValue(name))
            return this
        }

        public override fun applyUserName(name: String) {
            editor!!.putString(setHashKey(NAME), hideValue(name)).apply()
        }

        public override fun setFullName(fullName: String): UserChamber {
            editor!!.putString(setHashKey(FULLNAME), hideValue(fullName))
            return this
        }

        public override fun applyFullName(fullName: String) {
            editor!!.putString(setHashKey(FULLNAME), hideValue(fullName)).apply()
        }

        public override fun setFirstName(firstName: String): UserChamber {
            editor!!.putString(setHashKey(FIRST_NAME), hideValue(firstName))
            return this
        }

        public override fun applyFirstName(firstName: String) {
            editor!!.putString(setHashKey(FIRST_NAME), hideValue(firstName)).apply()
        }

        public override fun setLastName(lastName: String): UserChamber {
            editor!!.putString(setHashKey(LAST_NAME), hideValue(lastName))
            return this
        }

        public override fun applyLastName(lastName: String) {
            editor!!.putString(setHashKey(LAST_NAME), hideValue(lastName)).apply()
        }

        public override fun setAge(age: Int): UserChamber {
            editor!!.putString(setHashKey(AGE), hideValue(age.toString()))
            return this
        }

        public override fun applyAge(age: Int) {
            editor!!.putString(setHashKey(AGE), hideValue(age.toString())).apply()
        }

        public override fun setGender(gender: String): UserChamber {
            editor!!.putString(setHashKey(GENDER), hideValue(gender))
            return this
        }

        public override fun applyGender(gender: String) {
            editor!!.putString(setHashKey(GENDER), hideValue(gender)).apply()
        }

        public override fun setBirthDate(birthDate: String): UserChamber {
            editor!!.putString(setHashKey(BIRTH_DATE), hideValue(birthDate))
            return this
        }

        public override fun applyBirthDate(birthDate: String) {
            editor!!.putString(setHashKey(BIRTH_DATE), hideValue(birthDate)).apply()
        }

        public override fun setAddress(address: String): UserChamber {
            editor!!.putString(setHashKey(ADDRESS), hideValue(address))
            return this
        }

        public override fun applyAddress(address: String) {
            editor!!.putString(setHashKey(ADDRESS), hideValue(address)).apply()
        }

        public override fun setEmail(email: String): UserChamber {
            editor!!.putString(setHashKey(EMAIL), hideValue(email))
            return this
        }

        public override fun applyEmail(email: String) {
            editor!!.putString(setHashKey(EMAIL), hideValue(email)).apply()
        }

        public override fun setPushToken(pushToken: String): UserChamber {
            editor!!.putString(setHashKey(PUSH_TOKEN), hideValue(pushToken))
            return this
        }

        public override fun applyPushToken(pushToken: String) {
            editor!!.putString(setHashKey(PUSH_TOKEN), hideValue(pushToken)).apply()
        }

        public override fun setPhoneNumber(phoneNumber: String): UserChamber {
            editor!!.putString(setHashKey(PHONE_NO), hideValue(phoneNumber))
            return this
        }

        public override fun applyPhoneNumber(phoneNumber: String) {
            editor!!.putString(setHashKey(PHONE_NO), hideValue(phoneNumber)).apply()
        }

        public override fun setMobileNumber(mobileNumber: String): UserChamber {
            editor!!.putString(setHashKey(MOBILE_NO), hideValue(mobileNumber))
            return this
        }

        public override fun applyMobileNumber(mobileNumber: String) {
            editor!!.putString(setHashKey(MOBILE_NO), hideValue(mobileNumber)).apply()
        }

        public override fun setLogin(login: Boolean): UserChamber {
            editor!!.putString(setHashKey(HAS_LOGIN), hideValue(login.toString()))
            return this
        }

        public override fun applyLogin(login: Boolean) {
            editor!!.putString(setHashKey(HAS_LOGIN), hideValue(login.toString())).apply()
        }

        public override fun setPassword(password: String): UserChamber {
            editor!!.putString(setHashKey(PASSWORD), hideValue(password))
            return this
        }

        public override fun applyPassword(password: String) {
            editor!!.putString(setHashKey(PASSWORD), hideValue(password)).apply()
        }

        public override fun setFirstTimeUser(firstTimeUser: Boolean): UserChamber {
            editor!!.putString(setHashKey(FIRST_TIME_USER), hideValue(firstTimeUser.toString()))
            return this
        }

        public override fun applyFirstTimeUser(firstTimeUser: Boolean) {
            editor!!.putString(setHashKey(FIRST_TIME_USER), hideValue(firstTimeUser.toString())).apply()
        }

        public override fun setUserDetail(`object`: Any): UserChamber {
            editor!!.putString(setHashKey(USER_JSON), hideValue(Gson().toJson(`object`)))
            return this
        }

        public override fun applyUserDetail(`object`: Any) {
            editor!!.putString(setHashKey(USER_JSON), hideValue(Gson().toJson(`object`))).apply()
        }

        @CheckResult
        public override fun getUserDetail(typeOfT: Type): Any {
            val value = returnValue(USER_JSON)
            return Gson().fromJson(value, typeOfT)
        }

        @CheckResult
        public override fun getUserDetail(classOfT: Class<Any>): Any {
            val value = returnValue(USER_JSON)
            return Gson().fromJson(value, classOfT)
        }

        @CheckResult
        public override fun hasLogin(): Boolean? {
            return try {
                java.lang.Boolean.parseBoolean(returnValue(HAS_LOGIN))
            } catch (e: Exception) {
                e.printStackTrace()
                null
            }
        }
    }


    /******************************************
     * SharedPreferences Editor Builder
     */
    class Editor : BaseEditorAbstract<Editor> {

        constructor() : super(sharedPreferences) {
            setDefaultPrefix(defaultPrefix)
            this.secretChamber = BaseRepository.secretChamber
        }

        constructor(keyPrefix: String?) : super(keyPrefix, sharedPreferences) {
            if (keyPrefix == null) {
                setDefaultPrefix(defaultPrefix)
            }

            this.secretChamber = BaseRepository.secretChamber
        }

        public override fun put(key: String, value: String): Editor {
            editor!!.putString(setHashKey(key), hideValue(value))
            return this
        }

        public override fun  apply(key: String, value: String) {
            editor!!.putString(setHashKey(key), hideValue(value)).apply()
        }

        public override fun  put(key: String, value: Int): Editor {
            editor!!.putString(setHashKey(key), hideValue(Integer.toString(value)))
            return this
        }

        public override fun  apply(key: String, value: Int) {
            editor!!.putString(setHashKey(key), hideValue(Integer.toString(value))).apply()
        }

        public override fun  put(key: String, value: Long): Editor {
            editor!!.putString(setHashKey(key), hideValue(java.lang.Long.toString(value)))
            return this
        }

        public override fun  apply(key: String, value: Long) {
            editor!!.putString(setHashKey(key), hideValue(java.lang.Long.toString(value))).apply()
        }

        public override fun  put(key: String, value: Double): Editor {
            editor!!.putString(setHashKey(key), hideValue(java.lang.Double.toString(value)))
            return this
        }

        public override fun  apply(key: String, value: Double) {
            editor!!.putString(setHashKey(key), hideValue(java.lang.Double.toString(value))).apply()
        }

        public override fun  put(key: String, value: Float): Editor {
            editor!!.putString(setHashKey(key), hideValue(java.lang.Float.toString(value)))
            return this
        }

        public override fun  apply(key: String, value: Float) {
            editor!!.putString(setHashKey(key), hideValue(java.lang.Float.toString(value))).apply()
        }

        public override fun  put(key: String, value: Boolean): Editor {
            editor!!.putString(setHashKey(key), hideValue(java.lang.Boolean.toString(value)))
            return this
        }

        public override fun  apply(key: String, value: Boolean) {
            editor!!.putString(setHashKey(key), hideValue(java.lang.Boolean.toString(value))).apply()
        }

        public override fun  put(key: String, value: List<*>): Editor {
            editor!!.putString(setHashKey(key), hideValue(value.toString()))
            return this
        }

        public override fun  apply(key: String, value: List<*>) {
            editor!!.putString(setHashKey(key), hideValue(value.toString())).apply()
        }

        public override fun  put(key: String, bytes: ByteArray): Editor {
            editor!!.putString(setHashKey(key), hideValue(String(bytes)))
            return this
        }

        public override fun  apply(key: String, bytes: ByteArray) {
            editor!!.putString(setHashKey(key), hideValue(String(bytes))).apply()
        }

        @RequiresPermission(allOf = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        public override fun putDrawable(key: String, @DrawableRes resId: Int, context: Context): Editor {
            val bitmap = BitmapFactory.decodeResource(context.resources, resId)
            if (bitmap != null) {
                val imageFile = File(getImageDirectory(folderPath!!), "images_" + System.currentTimeMillis() + ".png")
                if (FileUtils.saveBitmap(imageFile, bitmap)) {
                    editor!!.putString(setHashKey(key), hideValue(secretChamber!!.lockVaultFile(imageFile, true)!!.absolutePath))
                }
            } else {
                throw RuntimeException(resId.toString() + " : Drawable not found!")
            }
            return this
        }

        @RequiresPermission(allOf = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        public override fun  applyDrawable(key: String, @DrawableRes resId: Int, context: Context) {
            val bitmap = BitmapFactory.decodeResource(context.resources, resId)
            if (bitmap != null) {
                val imageFile = File(getImageDirectory(folderPath!!), "images_" + System.currentTimeMillis() + ".png")
                if (FileUtils.saveBitmap(imageFile, bitmap)) {
                    editor!!.putString(setHashKey(key), hideValue(secretChamber!!.lockVaultFile(imageFile, true)!!.absolutePath)).apply()
                }
            } else {
                throw RuntimeException(resId.toString() + " : Drawable not found!")
            }
        }

        @RequiresPermission(allOf = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        public override fun  put(key: String, bitmap: Bitmap): Editor {
            val imageFile = File(getImageDirectory(folderPath!!), "images_" + System.currentTimeMillis() + ".png")
            if (FileUtils.saveBitmap(imageFile, bitmap)) {
                editor!!.putString(setHashKey(key), hideValue(secretChamber!!.lockVaultFile(imageFile, true)!!.absolutePath))
            }
            return this
        }

        @RequiresPermission(allOf = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        public override fun  apply(key: String, bitmap: Bitmap) {
            val imageFile = File(getImageDirectory(folderPath!!), "images_" + System.currentTimeMillis() + ".png")
            if (FileUtils.saveBitmap(imageFile, bitmap)) {
                editor!!.putString(setHashKey(key), hideValue(secretChamber!!.lockVaultFile(imageFile, true)!!.absolutePath)).apply()
            }
        }

        @RequiresPermission(allOf = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        public override fun  put(key: String, file: File): Editor {
            if (FileUtils.isFileForImage(file)) {
                val imageFile = FileUtils.moveFile(file, getImageDirectory(folderPath!!))
                if (imageFile != null && imageFile.exists()) {
                    secretChamber!!.lockVaultFile(imageFile, true)
                    editor!!.putString(setHashKey(key), hideValue(imageFile.absolutePath))
                }
            }
            return this
        }

        @RequiresPermission(allOf = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        public override fun  apply(key: String, file: File) {
            if (FileUtils.isFileForImage(file)) {
                val imageFile = FileUtils.moveFile(file, getImageDirectory(folderPath!!))
                if (imageFile != null && imageFile.exists()) {
                    secretChamber!!.lockVaultFile(imageFile, true)
                    editor!!.putString(setHashKey(key), hideValue(imageFile.absolutePath)).apply()
                }
            }
        }

        @RequiresPermission(allOf = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        public override fun  put(key: String, file: File, deleteOldFile: Boolean): Editor {
            try {
                if (file.exists() && !FileUtils.isFileForImage(file)) {
                    val enc = secretChamber!!.lockVaultFile(file, deleteOldFile)
                    editor!!.putString(setHashKey(key), hideValue(enc!!.absolutePath))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

            return this
        }

        @RequiresPermission(allOf = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE))
        public override fun  apply(key: String, file: File, deleteOldFile: Boolean) {
            try {
                if (file.exists() && !FileUtils.isFileForImage(file)) {
                    val enc = secretChamber!!.lockVaultFile(file, deleteOldFile)
                    editor!!.putString(setHashKey(key), hideValue(enc!!.absolutePath)).apply()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        public override fun  put(key: String, values: Map<String, String>): Editor {
            editor!!.putString(setHashKey(key), hideValue(ConverterListUtils.convertMapToString(values)))
            return this
        }

        public override fun  apply(key: String, values: Map<String, String>) {
            editor!!.putString(setHashKey(key), hideValue(ConverterListUtils.convertMapToString(values))).apply()
        }

        public override fun  putModel(key: String, `object`: Any): Editor {
            put(key, Gson().toJson(`object`))
            return this
        }

        public override fun  applyModel(key: String, `object`: Any) {
            put(key, Gson().toJson(`object`)).apply()
        }


        public override fun  remove(key: String): Editor {
            editor!!.remove(setHashKey(key))
            return this
        }

        public override fun  clear(): Editor {
            editor!!.clear()
            return this
        }
    }


    /************************v***************************************************************
     * Preferences builder,  SharedChamber.ChamberBuilder
     */
    class ChamberBuilder(context: Context) : BasePreferencesBuilder<ChamberBuilder>(context) {

        public override fun useThisPrefStorage(mPrefname: String): ChamberBuilder {
            this.prefName = mPrefname
            return this
        }

        /**
         * Enable encryption for keys-values
         * @param encryptKey true/false to enable encryption for key
         * @param encryptValue true/false to enable encryption for values
         * @return ChamberBuilder
         */
        public override fun enableCrypto(encryptKey: Boolean, encryptValue: Boolean): ChamberBuilder {
            this.isEnabledCrypto = encryptValue
            this.isEnableCryptKey = encryptKey
            return this
        }

        /**
         * Use Conceal keychain
         * @param keyChain Cryptography type
         * @return ChamberBuilder
         */
        public override fun setChamberType(keyChain: ChamberType): ChamberBuilder {
            this.keyChain = keyChain
            return this
        }

        public override fun enableKeyPrefix(enable: Boolean, defaultPrefix: String?): ChamberBuilder {
            this.defaultPrefix = if (enable) {
                if (false) {
                    Constant.PREFIX
                } else {
                    defaultPrefix!!
                }
            } else {
                ""
            }
            return this
        }

        /**
         * Setup password / paraphrase for encryption
         * @param password string password
         * @return ChamberBuilder
         */
        public override fun setPassword(password: String): ChamberBuilder {
            entityPasswordRaw = password
            return this
        }

        /**
         * Set folder name to store files and images
         * @param folderName folder path
         * @return ChamberBuilder
         */
        public override fun setFolderName(folderName: String): ChamberBuilder {
            setmFolderName(folderName)
            return this
        }

        /**
         * Listen to data changes
         * @param listener OnDataChamberChangeListener listener
         * @return ChamberBuilder
         */
        public override fun setPrefListener(listener: OnDataChamberChangeListener): ChamberBuilder {
            onDataChangeListener = listener
            return this
        }

        /**
         * Create Preferences
         * @return SharedChamber
         */

        fun buildChamber(): SharedChamber {

            if (context == null) {
                throw RuntimeException("Context cannot be null")
            }

            if (getFolderName() != null) {
                val file = File(getFolderName()!!)
                try {
                    file.canonicalPath
                    val newFolder = if (getFolderName()!!.startsWith("")) getFolderName()!!.substring(1) else getFolderName()
                    setmFolderName(newFolder!!)
                } catch (e: IOException) {
                    e.printStackTrace()
                    throw RuntimeException("Folder Name is not Valid", e)
                }

            } else {
                setmFolderName(DEFAULT_MAIN_FOLDER)
            }

            sharedPreferences = if (prefName != null) {
                context!!.getSharedPreferences(CipherUtils.obscureEncodeSixFourString(prefName!!.toByteArray()), MODE_PRIVATE)
            } else {
                PreferenceManager.getDefaultSharedPreferences(context)
            }

            return SharedChamber(this)
        }
    }

    companion object {

        /***
         * Since Conceal facebook v2.0.+ (2017-06-27) you will need to initialize the native library loader.
         * This step is needed because the library loader uses the context.
         * The highly suggested way to do it is in the application class onCreate method like this:
         * @param application - Application Context ex: this
         */

        @JvmStatic
        fun initChamber(application: Application) {
            SoLoader.init(application, false)
        }
    }
}
