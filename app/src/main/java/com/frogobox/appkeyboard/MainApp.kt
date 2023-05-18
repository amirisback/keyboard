package com.frogobox.appkeyboard

import android.content.Context
import android.os.Build
import androidx.emoji2.bundled.BundledEmojiCompatConfig
import androidx.emoji2.text.EmojiCompat
import com.frogobox.appkeyboard.di.koin.databaseModule
import com.frogobox.appkeyboard.di.koin.delegateModule
import com.frogobox.appkeyboard.di.koin.repositoryModule
import com.frogobox.appkeyboard.di.koin.viewModelModule
import com.frogobox.sdk.FrogoKoinApplication
import dagger.hilt.android.HiltAndroidApp
import org.koin.core.KoinApplication
import java.util.Locale

/**
 * Created by Faisal Amir on 24/10/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */

@HiltAndroidApp
class MainApp : FrogoKoinApplication() {

    companion object {
        val TAG: String = MainApp::class.java.simpleName

        lateinit var instance: MainApp

        fun getContext(): Context = instance.applicationContext

        fun getCurrentLocale(): Locale? {
            return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                instance.resources.configuration.locales[0]
            } else {
                instance.resources.configuration.locale
            }
        }

    }

    override fun setupKoinModule(koinApplication: KoinApplication) {
        koinApplication.modules(
            listOf(
                delegateModule,
                repositoryModule,
                databaseModule,
                viewModelModule,
            )
        )
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        setupEmojiCompat()
    }

    private fun setupEmojiCompat() {
        val config = BundledEmojiCompatConfig(this)
        EmojiCompat.init(config)
    }

}