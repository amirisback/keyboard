package com.frogobox.appkeyboard.di.hilt

import android.content.Context
import com.frogobox.appkeyboard.util.Constant.PREF_ROOT_NAME
import com.frogobox.sdk.delegate.preference.PreferenceDelegates
import com.frogobox.sdk.delegate.preference.PreferenceDelegatesImpl
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
class UtilModule {

    @Provides
    @Singleton
    fun providePreferenceDelegates(@ApplicationContext context: Context): PreferenceDelegates {
        return PreferenceDelegatesImpl(context, PREF_ROOT_NAME)
    }

}