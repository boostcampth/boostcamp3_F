package com.boostcamp.travery.utils

import android.content.Context
import android.graphics.Bitmap
import org.json.JSONObject
import java.io.File

object FileUtils {
    fun saveJsonFile(context: Context, fileName: String, jsonObj: JSONObject) {
        context.openFileOutput("$fileName.json", Context.MODE_PRIVATE).use {
            it.write(jsonObj.toString().toByteArray())
        }
    }

    fun saveImageFile(context: Context, fileName: String, bitmap: Bitmap) {
        context.openFileOutput("$fileName.jpg", Context.MODE_PRIVATE).use {
            it.write(bitmap.ninePatchChunk)
        }
    }

    fun loadStringFromJsonFile(context: Context, fileName: String): String {
        val directory = context.filesDir
        val file = File(directory, "$fileName.json")

        return file.readText()
    }

    fun deleteCourseFile(context: Context, fileName: String) {
        val directory = context.filesDir
        val fileList = File(directory, "").listFiles()

        for (i in 0 until fileList.size) {
            if (fileList[i].name.contains(fileName)) {
                fileList[i].delete()
            }
        }
    }
}