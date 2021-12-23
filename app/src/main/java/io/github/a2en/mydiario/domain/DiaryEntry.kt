package io.github.a2en.mydiario.domain

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.squareup.moshi.Json
import java.util.*

@Entity
data class DiaryEntry(
    @Json(name = "_id")
    @PrimaryKey
    val id: String,
    val title: String?,
    val body: String?,
    val place: String?,
    val date: String?
):Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()?:"",
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString()
    ) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(title)
        parcel.writeString(body)
        parcel.writeString(place)
        parcel.writeString(date)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<DiaryEntry> {
        override fun createFromParcel(parcel: Parcel): DiaryEntry {
            return DiaryEntry(parcel)
        }

        override fun newArray(size: Int): Array<DiaryEntry?> {
            return arrayOfNulls(size)
        }
    }
}