package com.example.projectui.database.entity

import android.database.Cursor
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import kotlinx.parcelize.Parcelize
import org.json.JSONObject

@Entity(primaryKeys = ["id", "type"])
@Parcelize
data class Device (
    @ColumnInfo(name = "id") val id: Int,
    @ColumnInfo(name = "url") val url: String,
    @ColumnInfo(name = "type") val type: String,
    @ColumnInfo(name = "location") val location: String,
    @ColumnInfo(name = "techDesc") val techDesc: String,
    @ColumnInfo(name = "custDesc") var custDesc: String,
 ) : Parcelable {

    constructor(deviceObject: JSONObject) : this(
        id = deviceObject.getInt("id"),
        url = deviceObject.getString("url"),
        type = deviceObject.getString("type"),
        location = deviceObject.getString("location"),
        techDesc = deviceObject.getString("description"),
        custDesc = "none"
    )

    constructor(cursor: Cursor) : this(
        id = cursor.getInt(cursor.getColumnIndexOrThrow("id")),
        url = cursor.getString(cursor.getColumnIndexOrThrow("url")),
        type = cursor.getString(cursor.getColumnIndexOrThrow("type")),
        location = cursor.getString(cursor.getColumnIndexOrThrow("location")),
        techDesc = cursor.getString(cursor.getColumnIndexOrThrow("techDesc")),
        custDesc = cursor.getString(cursor.getColumnIndexOrThrow("custDesc")),
    )
}



