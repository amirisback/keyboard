package com.frogobox.keyboard.data.local.sample

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

/**
 * Created by Faisal Amir on 06/01/23
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */


@Entity(tableName = SampleAttr.TABLE_NAME)
data class SampleEntity(

    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = SampleAttr.ATTR_TABLE_ID)
    var table_id: Int = 0,

    @ColumnInfo(name = SampleAttr.ATTR_ID)
    var id: Int = 0,

    @ColumnInfo(name = SampleAttr.ATTR_DATA)
    @SerializedName("data")
    var data: String = "",

)