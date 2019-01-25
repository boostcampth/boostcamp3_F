package com.boostcamp.travery.data.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

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
) {
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "seq") var seq: Int? = null
}