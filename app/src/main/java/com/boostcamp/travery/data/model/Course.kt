package com.boostcamp.travery.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "course")
data class Course(
    @ColumnInfo(name = "title") var title: String?=null,
    @ColumnInfo(name = "body") var body: String?=null,
    @ColumnInfo(name = "theme") var theme: String?=null,
    @PrimaryKey @ColumnInfo(name = "start_time", index = true) var startTime: Long,
    @ColumnInfo(name = "end_time") var endTime: Long=0L,
    @ColumnInfo(name = "distance") var distance: Long?=null,
    @ColumnInfo(name = "coordinate") var coordinate: String?=null,
    @ColumnInfo(name = "map_image") var mapImage: String?=null
) : Parcelable