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
        @ColumnInfo(name = "title") var title: String = "",
        @ColumnInfo(name = "body") var body: String = "",
        @ColumnInfo(name = "date") var date: Date = Date(),
        @ColumnInfo(name = "hashtag") var hashTag: String = "",
        @ColumnInfo(name = "main_image") var mainImage: String = "",
        @ColumnInfo(name = "sub_image") var subImage: String = "",
        @ColumnInfo(name = "latitude") var latitude: Double,
        @ColumnInfo(name = "longitude") var longitude: Double,
        @ColumnInfo(name = "course_code", index = true) var courseCode: Long? = null,
        @ColumnInfo(name = "address") var address: String = " "
) : Parcelable {
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "seq")
    var seq: Int? = null

    override fun equals(other: Any?): Boolean {
        if (other is UserAction) {
            return this.seq == other.seq
        }
        return super.equals(other)
    }
}

data class FireUserAction(
    var title: String="",
    var body: String="",
    var date: String="",
    var hashTag: String="",
    var mainImage: String="",
    var subImage: String="",
    var latitude: Double,
    var longitude: Double,
    var like:Int = 0
)