package com.frogobox.appkeyboard.di.koin

import com.frogobox.appkeyboard.data.local.db.AppDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/**
 * Created by Faisal Amir on 11/03/23
 * https://github.com/amirisback
 */


val databaseModule = module {

    single {
        AppDatabase.newInstance(androidContext()).autoTextDao()
    }


}