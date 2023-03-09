package com.frogobox.keyboard.di

import com.frogobox.keyboard.repository.sample.SampleLocalSources
import com.frogobox.keyboard.repository.sample.SampleLocalSourcesImpl
import com.frogobox.keyboard.repository.sample.SampleRemoteSources
import com.frogobox.keyboard.repository.sample.SampleRemoteSourcesImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Created by Faisal Amir on 06/01/23
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */

@Module(includes = [
    NetworkModule::class,
    ServiceModule::class,
    DatabaseModule::class,
    UtilModule::class])
@InstallIn(SingletonComponent::class)
abstract class SourceModule {

    @Binds
    abstract fun getSampleRemoteDataSources(sources: SampleRemoteSourcesImpl): SampleRemoteSources

    @Binds
    abstract fun getSampleLocalDataSources(sources: SampleLocalSourcesImpl): SampleLocalSources

}