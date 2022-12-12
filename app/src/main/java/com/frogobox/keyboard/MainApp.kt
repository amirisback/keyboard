package com.frogobox.keyboard

import android.app.Application
import android.content.Context
import android.os.Build
import androidx.emoji2.bundled.BundledEmojiCompatConfig
import androidx.emoji2.text.EmojiCompat
import com.frogobox.sdk.FrogoApplication
import org.koin.core.KoinApplication
import java.util.*

/**
 * Created by Faisal Amir on 24/10/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */

class MainApp : FrogoApplication() {

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

    override fun onCreate() {
        super.onCreate()
        instance = this
        setupEmojiCompat()
    }

    override fun setupKoinModule(koinApplication: KoinApplication) {
        koinApplication.modules(listOf())
    }

    private fun setupEmojiCompat() {
        val config = BundledEmojiCompatConfig(this)
        EmojiCompat.init(config)
    }

}