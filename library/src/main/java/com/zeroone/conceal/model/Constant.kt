package com.zeroone.conceal.model

import android.os.Environment

/**
 * @author : hafiq on 25/03/2017.
 */

object Constant {


    val UTF8 = "UTF-8"


    val DEFAULT_DIRECTORY = Environment.getExternalStorageDirectory().toString() + "/."
    val DEFAULT_FILES_FOLDER = "files"
    val DEFAULT_IMAGE_FOLDER = "images"
    val DEFAULT_PREFIX_FILENAME = "conceal_enc_"
    val PREFIX = "conceal"
    val DEFAULT_MAIN_FOLDER = "conceal_path"


    val NAME = "conceal.user.username"
    val FULLNAME = "conceal.user.fullname"
    val FIRST_NAME = "conceal.user.first_name"
    val LAST_NAME = "conceal.user.last_name"
    val AGE = "conceal.user.age"
    val GENDER = "conceal.user.gender"
    val BIRTH_DATE = "conceal.user.dob"
    val ADDRESS = "conceal.user.address"
    val EMAIL = "conceal.user.email"
    val PUSH_TOKEN = "conceal.user.push.token"
    val PHONE_NO = "conceal.user.phone_number"
    val MOBILE_NO = "conceal.conceal.user.mobile_number"
    val HAS_LOGIN = "conceal.user.has_login"
    val PASSWORD = "conceal.user.password"
    val FIRST_TIME_USER = "conceal.user.first_time"
    val USER_ID = "conceal.user.user_id"
    val USER_JSON = "conceal.user.json"


    val DEVICE_ID = "conceal.device.id"
    val DEVICE_IS_UPDATE = "conceal.device.is_update"
    val DEVICE_VERSION = "conceal.device.version"
    val DEVICE_OS = "conceal.device.os"
    val DEVICE_DETAIL = "conceal.device.detail"
}
