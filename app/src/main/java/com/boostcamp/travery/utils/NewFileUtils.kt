package com.boostcamp.travery.utils

import android.graphics.Bitmap
import com.boostcamp.travery.data.model.TimeCode
import com.google.android.gms.maps.model.LatLng
import org.json.JSONObject
import java.io.File

object NewFileUtils {
    fun saveJsonFile(directory: File, fileName: String, jsonObj: JSONObject) {
        val file = File(directory, "$fileName.json")
        file.outputStream().use {
            it.write(jsonObj.toString().toByteArray())
        }
    }

    fun saveImageFile(directory: File, fileName: String, bitmap: Bitmap) {
        val file = File(directory, "$fileName.jpg")
        file.outputStream().use {
            it.write(bitmap.ninePatchChunk)
        }
    }

    fun loadCoordinateListFromJsonFile(directory: File, fileName: String): List<TimeCode> {
        val file = File(directory, "$fileName.json")
        val timeCode = ArrayList<TimeCode>()
        val coordinateList = JSONObject(file.readText()).getJSONArray("coordinate")
        for (i in 0 until coordinateList.length()) {
            val item = coordinateList.getJSONObject(i)
            timeCode.add(TimeCode(LatLng(item.getDouble("lat"), item.getDouble("lng")), item.getLong("time")))
        }
        return timeCode
    }

    fun deleteCourseFile(directory: File, fileName: String) {
        val fileList = File(directory, "").listFiles()

        for (i in 0 until fileList.size) {
            if (fileList[i].name.contains(fileName)) {
                fileList[i].delete()
            }
        }
    }
}