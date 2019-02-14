package com.boostcamp.travery.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(tableName = "course")
data class Course(
    @ColumnInfo(name = "title") var title: String="",
    @ColumnInfo(name = "body") var body: String="",
    @ColumnInfo(name = "theme") var theme: String="",
    @PrimaryKey @ColumnInfo(name = "start_time", index = true) var startTime: Long,
    @ColumnInfo(name = "end_time") var endTime: Long=0L,
    @ColumnInfo(name = "distance") var distance: Long=0L,
    @ColumnInfo(name = "coordinate") var coordinate: String="",
    @ColumnInfo(name = "map_image") var mapImage: String=""
) : Parcelable