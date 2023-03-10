package com.frogobox.keyboard.ui.autotext

import com.frogobox.keyboard.common.base.BaseViewModel
import com.frogobox.keyboard.repository.autotext.AutoTextRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Faisal Amir on 11/03/23
 * https://github.com/amirisback
 */


@HiltViewModel
class AutoTextViewModel @Inject constructor(
    private val repository: AutoTextRepository
) : BaseViewModel() {


}