package com.frogobox.appkeyboard.model

/**
 * Created by Faisal Amir on 22/11/22
 * github.com/amirisback
 */

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize


@Keep
// Entity annotation to specify the table's name
@Entity(tableName = "auto_text")
// Parcelable annotation to make parcelable object
@Parcelize
data class AutoTextEntity(
    // PrimaryKey annotation to declare primary key with auto increment value
    // ColumnInfo annotation to specify the column's name
    @PrimaryKey(autoGenerate = true) @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "title")
    var title: String = "",

    @ColumnInfo(name = "label")
    var label: AutoTextLabel = AutoTextLabel.USER,

    @ColumnInfo(name = "date")
    var date: String = "",

    @ColumnInfo(name = "updatedDate")
    var updatedDate: String = "",

    @ColumnInfo(name = "updatedTime")
    var updatedTime: String = "",

    @ColumnInfo(name = "body")
    var body: String = "",

    @ColumnInfo(name = "isActive")
    var isActive: Boolean = false

) : Parcelable