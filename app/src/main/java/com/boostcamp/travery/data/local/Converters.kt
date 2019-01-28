package com.boostcamp.travery.data.local

import androidx.room.TypeConverter
import java.util.*

class Converters {
    @TypeConverter
    fun fromTimestamp(value:Long?): Date?{
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimeStamp(date:Date?):Long?{
        return date?.time?.toLong()
    }
}