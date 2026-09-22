package com.frogobox.appkeyboard.di

import com.frogobox.appkeyboard.repository.autotext.AutoTextRepository
import com.frogobox.appkeyboard.repository.autotext.AutoTextRepositoryImpl
import com.frogobox.appkeyboard.repository.clipboard.ClipboardRepository
import com.frogobox.appkeyboard.repository.clipboard.ClipboardRepositoryImpl
import com.frogobox.appkeyboard.repository.data.DataApiRepository
import com.frogobox.appkeyboard.repository.data.DataApiRepositoryImpl
import com.frogobox.appkeyboard.repository.productremote.ProductRemoteRepository
import com.frogobox.appkeyboard.repository.productremote.ProductRemoteRepositoryImpl
import com.frogobox.appkeyboard.repository.templatetext.TemplateTextRepository
import com.frogobox.appkeyboard.repository.templatetext.TemplateTextRepositoryImpl
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Hilt module binding repository implementations to their interfaces.
 */
@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    abstract fun getAutoTextRepository(repository: AutoTextRepositoryImpl): AutoTextRepository

    @Binds
    @Singleton
    abstract fun bindDataApiRepository(repository: DataApiRepositoryImpl): DataApiRepository

    @Binds
    @Singleton
    abstract fun bindProductRemoteRepository(repository: ProductRemoteRepositoryImpl): ProductRemoteRepository

    @Binds
    @Singleton
    abstract fun bindClipboardRepository(repository: ClipboardRepositoryImpl): ClipboardRepository

    @Binds
    @Singleton
    abstract fun bindTemplateTextRepository(repository: TemplateTextRepositoryImpl): TemplateTextRepository

}