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
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "body") var body: String?,
    @ColumnInfo(name = "date") var date: Date?,
    @ColumnInfo(name = "hashtag") var hashTag: String?,
    @ColumnInfo(name = "main_image") var mainImage: String?,
    @ColumnInfo(name = "sub_image") var subImage: String?,
    @ColumnInfo(name = "latitude") var latitude: Long,
    @ColumnInfo(name = "longitude") var longitude: Long,
    @ColumnInfo(name = "course_code", index = true) var courseCode: Long?
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "seq")
    var seq: Int? = null
}