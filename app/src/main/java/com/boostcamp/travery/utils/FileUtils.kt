package com.boostcamp.travery.utils

import android.content.Context
import android.graphics.Bitmap
import org.json.JSONObject
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

object FileUtils {
    fun saveJsonFile(context: Context, dirName: String, fileName: String, jsonObj : JSONObject){
        val fullName = if(!dirName.isEmpty()) "$dirName/$fileName" else fileName
        context.openFileOutput("$fileName.json", Context.MODE_PRIVATE).use {
            it.write(jsonObj.toString().toByteArray())
        }
    }

    fun saveImageFile(context: Context, dirName: String, fileName: String, bitmap : Bitmap){
        val fullName = if(!dirName.isEmpty()) "$dirName/$fileName" else fileName
        context.openFileOutput("$fileName.jpg", Context.MODE_PRIVATE).use {
            it.write(bitmap.ninePatchChunk)
        }
    }

    fun loadStringFromJsonFile(context: Context, fileName: String): String{
        val directory = context.filesDir
        val file = File(directory, "$fileName.json")

        return file.readText()
    }
}