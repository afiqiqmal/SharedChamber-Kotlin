package com.zeroone.concealexample

import android.content.Context

import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

import java.io.IOException
import java.nio.charset.Charset
import java.util.ArrayList

/**
 * Created by hafiq on 15/12/2017.
 */

object Data {

    fun getTaskData(context: Context): List<Task> {
        return Gson().fromJson(loadJSONFromAsset(context, "task.json"), object : TypeToken<ArrayList<Task>>() {

        }.type)
    }

    fun getUser(context: Context): User {
        return Gson().fromJson(loadJSONFromAsset(context, "users.json"), User::class.java)
    }

    fun loadJSONFromAsset(context: Context, filename: String): String? {
        val json: String?
        try {
            val assets = context.assets.open(filename)
            val size = assets.available()
            val buffer = ByteArray(size)
            assets.read(buffer)
            assets.close()
            json = String(buffer, Charset.defaultCharset())
        } catch (ex: IOException) {
            ex.printStackTrace()
            return null
        }

        return json
    }
}
