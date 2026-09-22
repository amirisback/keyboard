package com.frogobox.appkeyboard.data.local.db

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.frogobox.appkeyboard.BuildConfig
import com.frogobox.appkeyboard.data.local.autotext.AutoTextDao
import com.frogobox.appkeyboard.data.local.productremote.ProductRemoteDao
import com.frogobox.appkeyboard.model.AutoTextEntity
import com.frogobox.appkeyboard.model.ProductEntity

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
        AutoTextEntity::class,
        ProductEntity::class
    ], version = 3,
    exportSchema = true
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun autoTextDao(): AutoTextDao
    abstract fun productRemoteDao(): ProductRemoteDao

    companion object {

        private const val DATABASE_NAME = BuildConfig.DATABASE_NAME

        @Volatile
        private var INSTANCE: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE TABLE IF NOT EXISTS `product_remote` (
                        `id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL,
                        `remoteId` TEXT,
                        `uploadTimestamp` TEXT,
                        `productName` TEXT NOT NULL,
                        `caption` TEXT NOT NULL,
                        `originalFileName` TEXT,
                        `fileSize` TEXT,
                        `fileType` TEXT,
                        `driveLink` TEXT,
                        `driveFileId` TEXT,
                        `thumbnailUrl` TEXT,
                        `previewUrl` TEXT,
                        `isVideo` INTEGER NOT NULL,
                        `statusDownload` TEXT NOT NULL,
                        `rowIndex` INTEGER,
                        `createdAt` INTEGER NOT NULL,
                        `updatedAt` INTEGER NOT NULL
                    )
                    """.trimIndent()
                )
            }
        }

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    CREATE INDEX IF NOT EXISTS `index_product_remote_remoteId` ON `product_remote` (`remoteId`)
                    """.trimIndent()
                )
            }
        }

        fun newInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: buildDatabase(context).also { INSTANCE = it }
            }
        }

        private fun buildDatabase(context: Context): AppDatabase {
            return if (BuildConfig.DEBUG) {
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DATABASE_NAME)
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .fallbackToDestructiveMigration(dropAllTables = true) // FOR DEVELOPMENT ONLY !!!!
                    .build()
            } else {
                Room.databaseBuilder(context.applicationContext, AppDatabase::class.java, DATABASE_NAME)
                    .addMigrations(MIGRATION_1_2, MIGRATION_2_3)
                    .build()
            }
        }

    }
}