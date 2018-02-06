package com.chamber.kotlin.library

import android.content.SharedPreferences

import java.lang.reflect.Type

/**
* @author by hafiq on 28/01/2018.
*/

abstract class UserAbstract<T : UserAbstract<T>> : BaseBuilderAbstract {

    protected abstract val userId: String
    protected abstract val userDetail: String
    protected abstract val userName: String
    protected abstract val fullName: String
    protected abstract val firstName: String
    protected abstract val lastName: String
    protected abstract val age: Int?
    protected abstract val gender: String
    protected abstract val birthDate: String
    protected abstract val address: String
    protected abstract val email: String
    protected abstract val pushToken: String
    protected abstract val phoneNumber: String
    protected abstract val mobileNumber: String
    protected abstract val password: String
    protected abstract val isFirstTimeUser: Boolean?

    constructor(sharedPreferences: SharedPreferences) : super(sharedPreferences) {}

    constructor(keyPrefix: String?, sharedPreferences: SharedPreferences) : super(keyPrefix, sharedPreferences) {}

    constructor(keyPrefix: String?, defaultEmptyValue: String?, sharedPreferences: SharedPreferences) : super(keyPrefix, defaultEmptyValue, sharedPreferences) {}

    protected abstract fun setDefault(defaultEmptyValue: String?): T
    protected abstract fun setUserDetail(user_detail: String): T
    protected abstract fun applyUserDetail(user_detail: String)
    protected abstract fun setUserId(user_id: String): T
    protected abstract fun applyUserId(user_id: String)
    protected abstract fun setUserName(name: String): T
    protected abstract fun setFullName(fullName: String): T
    protected abstract fun applyUserName(name: String)
    protected abstract fun applyFullName(fullName: String)
    protected abstract fun setFirstName(firstName: String): T
    protected abstract fun setLastName(lastName: String): T
    protected abstract fun setAge(age: Int): T
    protected abstract fun setGender(gender: String): T
    protected abstract fun setBirthDate(birthDate: String): T
    protected abstract fun setAddress(address: String): T
    protected abstract fun setEmail(email: String): T
    protected abstract fun setPushToken(pushToken: String): T
    protected abstract fun setPhoneNumber(phoneNumber: String): T
    protected abstract fun setMobileNumber(mobileNumber: String): T
    protected abstract fun setLogin(login: Boolean): T
    protected abstract fun setPassword(password: String): T
    protected abstract fun setFirstTimeUser(firstTimeUser: Boolean): T
    protected abstract fun setUserDetail(`object`: Any): T

    protected abstract fun applyFirstName(firstName: String)
    protected abstract fun applyLastName(lastName: String)
    protected abstract fun applyAge(age: Int)
    protected abstract fun applyGender(gender: String)
    protected abstract fun applyBirthDate(birthDate: String)
    protected abstract fun applyAddress(address: String)
    protected abstract fun applyEmail(email: String)
    protected abstract fun applyPushToken(pushToken: String)
    protected abstract fun applyPhoneNumber(phoneNumber: String)
    protected abstract fun applyMobileNumber(mobileNumber: String)
    protected abstract fun applyLogin(login: Boolean)
    protected abstract fun applyPassword(password: String)
    protected abstract fun applyFirstTimeUser(firstTimeUser: Boolean)
    protected abstract fun applyUserDetail(`object`: Any)
    protected abstract fun getUserDetail(typeOfT: Type): Any
    protected abstract fun getUserDetail(classOfT: Class<Any>): Any
    protected abstract fun hasLogin(): Boolean?

}
