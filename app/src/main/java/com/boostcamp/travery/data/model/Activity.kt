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
    tableName = "activity",
    foreignKeys = [ForeignKey(
        entity = Route::class,
        parentColumns = ["seq"],
        childColumns = ["route_code"],
        onDelete = ForeignKey.SET_NULL
    )]
)
data class Activity(
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "body") var body: String?,
    @ColumnInfo(name = "date") var date: Date?,
    @ColumnInfo(name = "hashtag") var hashTag: String?,
    @ColumnInfo(name = "main_image") var mainImage: String?,
    @ColumnInfo(name = "sub_image") var subImage: String?,
    @ColumnInfo(name = "latitude") var latitude: Long,
    @ColumnInfo(name = "longitude") var longitude: Long,
    @ColumnInfo(name = "route_code",index = true) var routeCode: Int?
) : Parcelable {
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "seq") var seq: Int? = null
}