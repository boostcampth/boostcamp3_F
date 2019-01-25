package com.boostcamp.travery.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "activity",
    foreignKeys = arrayOf(
        ForeignKey(
            entity = Route::class,
            parentColumns = arrayOf("seq"),
            childColumns = arrayOf("route_code"),
            onDelete = ForeignKey.SET_NULL
        )
    )
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
    @ColumnInfo(name = "route_code") var routeCode: Int?
) {
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "seq") var seq: Int? = null
}