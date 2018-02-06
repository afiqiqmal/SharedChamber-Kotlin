package com.chamber.kotlin.sample

import android.os.Bundle
import android.util.Log

import com.google.gson.reflect.TypeToken
import com.chamber.kotlin.library.SecretBuilder
import com.chamber.kotlin.library.SharedChamber
import com.chamber.kotlin.library.model.ChamberType
import com.zeroone.concealexample.R

import java.util.ArrayList


class MainActivity : BaseActivity() {

    private var NAME_KEY = "user_name"
    private var AGE_KEY = "user_age"
    private var EMAIL_KEY = "user_email"
    private var USER_DETAIL = "user_detail"
    private var TASK_DETAIL = "task_detail"
    private var IMAGE_KEY = "user_image"
    private var FILE_KEY = "user_file"
    private var PREFIX: String? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        SharedChamber.initChamber(application)

        chamber.clearChamber()

        val floats = ArrayList<Float>()
        floats.add(10f)
        floats.add(10f)
        floats.add(10f)

        //FIRST TEST
        chamber.put(NAME_KEY, "HAFIQ IQMAL")
        chamber.put(AGE_KEY, floats)
        chamber.putModel(USER_DETAIL, Data.getUser(this))
        chamber.putModel(TASK_DETAIL, Data.getTaskData(this))

        Log.d("FIRST TEST", chamber.getString(NAME_KEY))
        Log.d("FIRST TEST", chamber.getListFloat(AGE_KEY)!!.toString())
        Log.d("FIRST TEST", chamber.getModel(USER_DETAIL, User::class.java).toString())
        Log.d("FIRST TEST", chamber.getModel(TASK_DETAIL, object : TypeToken<ArrayList<Task>>() {}.type).toString())
        Log.d("FIRST TEST SIZE", "" + chamber.chamberSize)

        chamber.clearChamber()

        //SECOND TEST
        SharedChamber.Editor()
                .put(NAME_KEY, "Hafiq Iqmal")
                .put(AGE_KEY, 24)
                .put(EMAIL_KEY, "hafiqiqmal93@gmail.com")
                .putModel("ALL", Data.getTaskData(this))
                .apply()

        Log.d("SECOND TEST", chamber.getString(NAME_KEY))
        Log.d("SECOND TEST", chamber.getString(AGE_KEY))
        Log.d("SECOND TEST", chamber.getModel("ALL", object : TypeToken<ArrayList<Task>>() {}.type).toString())
        Log.d("SECOND TEST SIZE", "" + chamber.chamberSize)


        getList()


        //add user details preferences
        SharedChamber.UserChamber(PREFIX).setFirstName("Firstname").setLastName("Lasname").setEmail("hello@gmail.com").setUserDetail(Data.getUser(this)).apply()

        //get user details
        Log.d("THIRD_TEST", SharedChamber.UserChamber(PREFIX).firstName)
        Log.d("THIRD_TEST", SharedChamber.UserChamber().setDefault("No Data").lastName)
        Log.d("THIRD_TEST", SharedChamber.UserChamber(PREFIX).setDefault("No Data").email)
        Log.d("THIRD_TEST", SharedChamber.UserChamber(PREFIX).getUserDetail(User::class.java).toString())
        Log.d("THIRD_TEST TEST SIZE", "" + chamber.chamberSize)

        getList()


        val userPref = SharedChamber.UserChamber(PREFIX, "No Data")
        userPref.setUserName("afiqiqmal")
        userPref.setEmail("afiqiqmal@example.com")
        userPref.apply()

        //get user details
        Log.d("FOURTH_TEST", userPref.userName)
        Log.d("FOURTH_TEST", userPref.email)

        getList()


        val devicePref = SharedChamber.DeviceChamber(PREFIX, "No Data")
        devicePref.setDeviceId("ABC123123123")
        devicePref.setDeviceOS("android")
        devicePref.apply()

        //get user details
        Log.d("FIFTH_TEST", devicePref.deviceId)
        Log.d("FIFTH_TEST", devicePref.deviceOs)


        getList()

        val secretChamber = SecretBuilder(this)
                .setEnableValueEncryption(true) //default true
                .setEnableKeyEncryption(true) // default true
                .setChamberType(ChamberType.KEY_256) // ChamberType.KEY_256 or ChamberType.KEY_128
                .setPassword("Mac OSX")
                .buildSecret()

        var test = "Hello World"
        var cipher = secretChamber.lockVault(test) //encrypt
        Log.d("CYRPTO TEST E", cipher)
        var dec = secretChamber.openVault(cipher) //decrypt
        Log.d("CYRPTO TEST D", dec)

        test = "Hello World Iteration"
        cipher = secretChamber.lockVaultBase(test, 4) //encrypt with 4 times
        Log.d("CYRPTO TEST E", cipher)
        dec = secretChamber.openVaultBase(cipher!!, 4) //decrypt with 4 times
        Log.d("CYRPTO TEST D", dec)



        cipher = secretChamber.lockVaultAes("Hello World is World Hello Aes Cryption")
        Log.d("AES E", cipher)
        dec = secretChamber.openVaultAes(cipher!!)
        Log.d("AES D", dec)
    }


    private fun getList() {
        val mapList = chamber.everythingInChamberInList
        for (s in mapList) {
            try {
                Log.d("VIEW_LIST", s)
            } catch (e: Exception) {
                e.printStackTrace()
            }

        }

        Log.d("VIEW ALL SIZE", "" + chamber.chamberSize)
    }
}
