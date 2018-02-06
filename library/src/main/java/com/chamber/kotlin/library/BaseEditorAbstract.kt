package com.chamber.kotlin.library

import android.content.Context
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.support.annotation.DrawableRes

import java.io.File

/**
* @author by hafiq on 28/01/2018.
*/

abstract class BaseEditorAbstract<T : BaseEditorAbstract<T>> : BaseBuilderAbstract {

    var folderPath: String? = null
        private set

    internal constructor(sharedPreferences: SharedPreferences?) : super(sharedPreferences) {}

    internal constructor(keyPrefix: String?, sharedPreferences: SharedPreferences?) : super(keyPrefix, sharedPreferences) {}

    internal constructor(keyPrefix: String?, defaultEmptyValue: String?, sharedPreferences: SharedPreferences?) : super(keyPrefix, defaultEmptyValue, sharedPreferences) {}

    internal abstract fun put(key: String, value: String): T
    internal abstract fun apply(key: String, value: String)
    internal abstract fun put(key: String, value: Int): T
    internal abstract fun apply(key: String, value: Int)
    internal abstract fun put(key: String, value: Long): T
    internal abstract fun apply(key: String, value: Long)
    internal abstract fun put(key: String, value: Double): T
    internal abstract fun apply(key: String, value: Double)
    internal abstract fun put(key: String, value: Float): T
    internal abstract fun apply(key: String, value: Float)
    internal abstract fun put(key: String, value: Boolean): T
    internal abstract fun apply(key: String, value: Boolean)
    internal abstract fun put(key: String, value: List<*>): T
    internal abstract fun apply(key: String, value: List<*>)
    internal abstract fun put(key: String, bytes: ByteArray): T
    internal abstract fun apply(key: String, bytes: ByteArray)
    internal abstract fun putModel(key: String, `object`: Any): T
    internal abstract fun applyModel(key: String, `object`: Any)

    internal abstract fun putDrawable(key: String, @DrawableRes resId: Int, context: Context): T
    internal abstract fun applyDrawable(key: String, @DrawableRes resId: Int, context: Context)
    internal abstract fun put(key: String, bitmap: Bitmap): T
    internal abstract fun apply(key: String, bitmap: Bitmap)
    internal abstract fun put(key: String, file: File): T
    internal abstract fun apply(key: String, file: File)
    internal abstract fun put(key: String, file: File, deleteOldFile: Boolean): T
    internal abstract fun apply(key: String, file: File, deleteOldFile: Boolean)

    internal abstract fun put(key: String, values: Map<String, String>): T
    internal abstract fun apply(key: String, values: Map<String, String>)

    internal abstract fun remove(key: String): T
    internal abstract fun clear(): T


    internal fun setFolderName(folderPath: String) {
        this.folderPath = folderPath
    }
}
