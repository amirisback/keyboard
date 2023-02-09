package com.frogobox.research.di

import com.frogobox.keyboard.repository.sample.SampleRemoteSourcesImpl
import com.frogobox.research.repository.sample.*
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