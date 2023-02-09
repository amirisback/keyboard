package com.frogobox.keyboard.data.local.sample

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

/**
 * Created by Faisal Amir on 06/01/23
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */


@Dao
interface SampleDao {

    @Query("SELECT * FROM ${SampleAttr.TABLE_NAME}")
    fun getMusics(): Single<List<SampleEntity>>

    @Query("DELETE FROM ${SampleAttr.TABLE_NAME}")
    fun nukeData(): Completable

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertMusics(articles: List<SampleEntity>): Completable

}