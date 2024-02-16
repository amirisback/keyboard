package com.frogobox.appkeyboard.ui.theme

import com.frogobox.appkeyboard.common.base.BaseViewModel
import com.frogobox.appkeyboard.repository.autotext.AutoTextRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by faisalamircs on 16/02/2024
 * -----------------------------------------
 * Name     : Muhammad Faisal Amir
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 */


@HiltViewModel
class ThemeViewModel @Inject constructor(
    private val repository: AutoTextRepository
): BaseViewModel() {


}