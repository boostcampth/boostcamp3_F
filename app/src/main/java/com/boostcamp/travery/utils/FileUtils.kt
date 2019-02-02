package com.boostcamp.travery.utils

import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import com.boostcamp.travery.data.model.TimeCode
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

    fun loadCoordinateListFromJsonFile(context: Context, fileName: String): List<TimeCode> {
        val directory = context.filesDir
        val file = File(directory, "$fileName.json")
        val timeCode = ArrayList<TimeCode>()
        val coordinateList = JSONObject(file.readText()).getJSONArray("coordinate")
        for (i in 0 until coordinateList.length()) {
            val item = coordinateList.getJSONObject(i)
            timeCode.add(TimeCode(item.getDouble("lat"), item.getDouble("lng"), item.getLong("time")))
        }
        return timeCode
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