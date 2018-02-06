package com.chamber.kotlin.library

import com.chamber.kotlin.library.model.Constant.UTF8
import java.io.UnsupportedEncodingException
import java.net.URLDecoder
import java.net.URLEncoder
import java.util.*

/**
 * @author : hafiq on 23/03/2017.
 */

internal object ConverterListUtils {

    @JvmStatic
    fun toIntArray(string: String): List<Int>? {
        val strings = getArray(string)
        val result = arrayOfNulls<Int>(strings.size)
        return try {
            for (i in result.indices) {
                result[i] = Integer.parseInt(strings[i])
            }
            Arrays.asList<Int>(*result)
        } catch (e: Exception) {
            null
        }

    }

    @JvmStatic
    fun toBooleanArray(string: String): List<Boolean>? {
        val strings = getArray(string)
        val result = arrayOfNulls<Boolean>(strings.size)
        return try {
            for (i in result.indices) {
                result[i] = java.lang.Boolean.parseBoolean(strings[i])
            }
            Arrays.asList<Boolean>(*result)
        } catch (e: Exception) {
            null
        }
    }

    @JvmStatic
    fun toLongArray(string: String): List<Long>? {
        val strings = getArray(string)
        val result = arrayOfNulls<Long>(strings.size)
        return try {
            for (i in result.indices) {
                result[i] = java.lang.Long.parseLong(strings[i])
            }
            Arrays.asList<Long>(*result)
        } catch (e: Exception) {
            null
        }

    }

    @JvmStatic
    fun toDoubleArray(string: String): List<Double>? {
        val strings = getArray(string)
        val result = arrayOfNulls<Double>(strings.size)
        return try {
            for (i in result.indices) {
                result[i] = java.lang.Double.parseDouble(strings[i])
            }

            Arrays.asList<Double>(*result)
        } catch (e: Exception) {
            null
        }

    }

    @JvmStatic
    fun toFloatArray(string: String): List<Float>? {
        val strings = getArray(string)
        val result = arrayOfNulls<Float>(strings.size)
        return try {
            for (i in result.indices) {
                result[i] = java.lang.Float.parseFloat(strings[i])
            }
            Arrays.asList<Float>(*result)
        } catch (e: Exception) {
            null
        }
    }

    @JvmStatic
    fun toStringArray(string: String): List<String> {
        return Arrays.asList(*getArray(string))
    }

    private fun getArray(string: String): Array<String> {
        return string.replace("[", "").replace("]", "").split(", ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
    }

    @JvmStatic
    fun convertMapToString(maps: Map<String, String>): String {
        val stringBuilder = StringBuilder()

        for (key in maps.keys) {
            if (stringBuilder.isNotEmpty()) {
                stringBuilder.append("&")
            }
            val value = maps[key]
            try {
                stringBuilder.append(URLEncoder.encode(key, UTF8))
                stringBuilder.append("=")
                stringBuilder.append(if (value != null) URLEncoder.encode(value, UTF8) else "")
            } catch (e: UnsupportedEncodingException) {
                throw RuntimeException("This method requires UTF-8 encoding support", e)
            }

        }

        return stringBuilder.toString()
    }

    @JvmStatic
    fun convertStringToMap(input: String): LinkedHashMap<String, String> {
        val map = LinkedHashMap<String, String>()

        val nameValuePairs = input.split("&".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        nameValuePairs
                .map { nameValuePair -> nameValuePair.split("=".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray() }
                .forEach {
                    try {
                        map.put(URLDecoder.decode(it[0], UTF8), if (it.size > 1) URLDecoder.decode(it[1], UTF8) else "")
                    } catch (e: UnsupportedEncodingException) {
                        throw RuntimeException("This method requires UTF-8 encoding support", e)
                    }
                }

        return map
    }
}
