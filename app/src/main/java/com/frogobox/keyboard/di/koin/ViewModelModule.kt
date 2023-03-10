package com.frogobox.keyboard.di.koin

import com.frogobox.keyboard.ui.autotext.AutoTextViewModel
import com.frogobox.keyboard.ui.keyboard.autotext.AutoTextKeyboard
import com.frogobox.keyboard.ui.keyboard.autotext.AutoTextKeyboardViewModel
import org.koin.androidx.viewmodel.dsl.viewModel
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

val viewModelModule = module {

    viewModel {
        AutoTextViewModel(get())
    }

}