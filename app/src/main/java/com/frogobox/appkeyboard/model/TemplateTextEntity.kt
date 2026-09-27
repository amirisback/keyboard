package com.frogobox.appkeyboard.model

import android.os.Parcelable
import androidx.annotation.Keep
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.parcelize.Parcelize

/**
 * Room Entity representing a persistent template text item.
 */
@Keep
@Entity(
    tableName = "template_text",
    indices = [
        Index(value = ["category"])
    ]
)
@Parcelize
data class TemplateTextEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    var id: Int = 0,

    @ColumnInfo(name = "category")
    var category: String = "",

    @ColumnInfo(name = "text")
    var text: String = "",

    @ColumnInfo(name = "createdAt")
    var createdAt: Long = System.currentTimeMillis(),

    @ColumnInfo(name = "updatedAt")
    var updatedAt: Long = System.currentTimeMillis()
) : Parcelable
