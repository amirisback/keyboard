package com.frogobox.keyboard.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.frogobox.keyboard.BuildConfig
import com.frogobox.keyboard.data.local.autotext.AutoTextDao
import com.frogobox.keyboard.model.AutoTextEntity

/**
 * Created by Faisal Amir on 06/01/23
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */


@Database(
    entities = [
        AutoTextEntity::class
    ], version = 1,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun autoTextDao(): AutoTextDao

    companion object {

        private const val DATABASE_NAME = BuildConfig.DATABASE_NAME

        fun newInstance(context: Context): AppDatabase {
            return buildDatabase(context)
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return if (BuildConfig.DEBUG) {
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DATABASE_NAME)
                    .fallbackToDestructiveMigration() // FOR DEVELOPMENT ONLY !!!!
                    .build()
            } else {
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DATABASE_NAME)
                    .build()
            }
        }

    }
}