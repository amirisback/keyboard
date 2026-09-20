package com.frogobox.appkeyboard.di

import com.frogobox.appkeyboard.repository.autotext.AutoTextRepository
import com.frogobox.appkeyboard.repository.autotext.AutoTextRepositoryImpl
import com.frogobox.appkeyboard.repository.data.DataApiRepository
import com.frogobox.appkeyboard.repository.data.DataApiRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module binding repository implementations to their interfaces.
 */
@Module(includes = [
    NetworkModule::class,
    ServiceModule::class,
    DatabaseModule::class,
    UtilModule::class])
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun getAutoTextRepository(repository: AutoTextRepositoryImpl): AutoTextRepository

    @Binds
    @Singleton
    abstract fun bindDataApiRepository(repository: DataApiRepositoryImpl): DataApiRepository

}