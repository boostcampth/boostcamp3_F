package com.boostcamp.travery.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
@Entity(tableName = "route")
data class Route(
    @ColumnInfo(name = "title") var title: String?,
    @ColumnInfo(name = "body") var body: String?,
    @ColumnInfo(name = "theme") var theme: String?,
    @ColumnInfo(name = "start_time") var startTime: Long,
    @ColumnInfo(name = "end_time") var endTime: Long,
    @ColumnInfo(name = "distance") var distance: Long,
    @ColumnInfo(name = "coordinate") var coordinate: String?,
    @ColumnInfo(name = "map_image") var mapImage: String?
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "seq", index = true)
    var seq: Int? = null

    override fun toString(): String {
        return "title: $title, theme: $theme"
    }
}