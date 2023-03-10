package com.frogobox.keyboard.di

import android.content.Context
import com.frogobox.keyboard.data.local.AutoText.AutoTextDao
import com.frogobox.keyboard.data.local.db.AppDatabase
import com.frogobox.keyboard.data.local.sample.SampleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Created by Faisal Amir on 06/01/23
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */


@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext appContext: Context): AppDatabase {
        return AppDatabase.newInstance(appContext)
    }

    @Provides
    fun provideSampleDao(database: AppDatabase): SampleDao {
        return database.sampleDao()
    }

    @Provides
    fun provideAutoTextDao(database: AppDatabase): AutoTextDao {
        return database.autoTextDao()
    }

}