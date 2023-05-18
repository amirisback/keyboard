package com.frogobox.appkeyboard.model

import androidx.annotation.Keep

/**
 * Created by Faisal Amir on 04/12/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */

@Keep
sealed class SpinnerItemType {

    data class Loading(
        val isLoading: Boolean = true,
    ) : SpinnerItemType()

    data class Content(
        val data: String,
    ) : SpinnerItemType()

}