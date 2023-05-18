package com.frogobox.appkeyboard.di.koin

import com.frogobox.appkeyboard.repository.autotext.AutoTextRepositoryImpl
import org.koin.dsl.module


/**
 * Created by faisalamir on 05/03/22
 * PianoTiles
 * -----------------------------------------
 * Name     : Muhammad Faisal Amir
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) 2022 Frogobox Media Inc.      
 * All rights reserved
 *
 */

val repositoryModule = module {

    single {
        AutoTextRepositoryImpl(get())
    }


}