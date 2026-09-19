package com.frogobox.appkeyboard

import android.content.Context
import androidx.core.os.ConfigurationCompat
import androidx.emoji2.bundled.BundledEmojiCompatConfig
import androidx.emoji2.text.EmojiCompat
import com.frogobox.sdk.FrogoApplication
import dagger.hilt.android.HiltAndroidApp
import java.util.Locale
import java.util.concurrent.Executors

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
class MainApp : FrogoApplication() {

    companion object {

        lateinit var instance: MainApp

        fun getContext(): Context = instance.applicationContext

        fun getCurrentLocale(): Locale? {
            return ConfigurationCompat.getLocales(instance.resources.configuration)[0]
        }

    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        setupEmojiCompat()
    }

    private fun setupEmojiCompat() {
        val config = BundledEmojiCompatConfig(this, Executors.newSingleThreadExecutor())
        EmojiCompat.init(config)
    }

}