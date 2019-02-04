package com.boostcamp.travery.data.model

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
@Entity(
    tableName = "useraction",
    foreignKeys = [ForeignKey(
        entity = Course::class,
        parentColumns = ["start_time"],
        childColumns = ["course_code"],
        onDelete = ForeignKey.SET_NULL
    )]
)
data class UserAction(
    @ColumnInfo(name = "title") var title: String?=null,
    @ColumnInfo(name = "body") var body: String?=null,
    @ColumnInfo(name = "date") var date: Date?=null,
    @ColumnInfo(name = "hashtag") var hashTag: String?=null,
    @ColumnInfo(name = "main_image") var mainImage: String?=null,
    @ColumnInfo(name = "sub_image") var subImage: String?=null,
    @ColumnInfo(name = "latitude") var latitude: Double,
    @ColumnInfo(name = "longitude") var longitude: Double,
    @ColumnInfo(name = "course_code", index = true) var courseCode: Long?=null
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "seq")
    var seq: Int? = null
}