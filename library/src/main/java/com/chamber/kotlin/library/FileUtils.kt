package com.chamber.kotlin.library

import android.graphics.Bitmap
import com.chamber.kotlin.library.model.Constant.DEFAULT_DIRECTORY
import com.chamber.kotlin.library.model.Constant.DEFAULT_IMAGE_FOLDER
import com.chamber.kotlin.library.model.Constant.DEFAULT_PREFIX_FILENAME
import com.chamber.kotlin.library.model.CryptoFile
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.nio.channels.FileChannel
import java.util.*

/**
 * @author : hafiq on 24/03/2017.
 */

internal object FileUtils {
    @JvmStatic
    fun moveFile(file: File?, dir: File?): File? {
        if (dir == null || file == null)
            return null

        if (!dir.exists()) {
            if (dir.mkdirs()) {
                val newFile = File(dir, file.name)
                val outputChannel: FileChannel
                val inputChannel: FileChannel
                try {
                    outputChannel = FileOutputStream(newFile).channel
                    inputChannel = FileInputStream(file).channel
                    inputChannel.transferTo(0, inputChannel.size(), outputChannel)
                    inputChannel.close()

                    inputChannel.close()
                    outputChannel.close()

                    file.delete()
                } catch (e: Exception) {
                    return null
                }

                return newFile
            }
        }

        return file
    }

    @JvmStatic
    fun saveBitmap(imageFile: File, bitmap: Bitmap): Boolean {

        var fileCreated = false
        var bitmapCompressed: Boolean
        var streamClosed = false

        if (imageFile.exists())
            if (!imageFile.delete())
                return false

        try {
            fileCreated = imageFile.createNewFile()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        var out: FileOutputStream? = null
        try {
            out = FileOutputStream(imageFile)
            bitmapCompressed = bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)

        } catch (e: Exception) {
            e.printStackTrace()
            bitmapCompressed = false

        } finally {
            if (out != null) {
                try {
                    out.flush()
                    out.close()
                    streamClosed = true

                } catch (e: IOException) {
                    e.printStackTrace()
                    streamClosed = false
                }

            }
        }

        return fileCreated && bitmapCompressed && streamClosed
    }

    @JvmStatic
    fun isFileForImage(file: File?): Boolean {
        if (file == null)
            return false

        val okFileExtensions = arrayOf("jpg", "png", "gif", "jpeg")

        for (extension in okFileExtensions) {
            if (file.name.toLowerCase().endsWith(extension)) {
                return true
            }
        }
        return false
    }

    /***
     * get default directory
     * @return File
     */
    @JvmStatic
    fun getDirectory(mFolderName: String): File? {
        val file = File(DEFAULT_DIRECTORY + mFolderName + "/" + DEFAULT_IMAGE_FOLDER)
        return if (file.exists()) file else null

    }

    /***
     * get default folder
     * @return File
     */
    @JvmStatic
    fun getImageDirectory(mFolderName: String): File? {
        val file = File(DEFAULT_DIRECTORY + mFolderName + "/" + DEFAULT_IMAGE_FOLDER)
        if (file.mkdirs())
            return file
        return if (file.exists()) file else null

    }

    /***
     * get List of encrypted file
     * @param parentDir - root directory
     * @return File
     */
    @JvmStatic
    fun getListFiles(parentDir: File?): List<CryptoFile> {
        val inFiles = ArrayList<CryptoFile>()
        try {
            if (parentDir != null) {
                val files = parentDir.listFiles()
                for (file in files!!) {
                    if (file.isDirectory) {
                        inFiles.addAll(getListFiles(file))
                    } else {
                        if (file.name.startsWith(DEFAULT_PREFIX_FILENAME)) {
                            val cryptoFile = CryptoFile()
                            cryptoFile.fileName = file.name
                            cryptoFile.path = file.absolutePath
                            cryptoFile.type = file.parent
                            inFiles.add(cryptoFile)
                        }
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return inFiles
    }
}
