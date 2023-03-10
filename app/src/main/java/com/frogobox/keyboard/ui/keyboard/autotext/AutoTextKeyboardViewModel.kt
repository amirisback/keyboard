package com.frogobox.keyboard.ui.keyboard.autotext

import com.frogobox.coresdk.util.FrogoDate
import com.frogobox.keyboard.data.local.autotext.AutoTextEntity
import com.frogobox.keyboard.data.local.autotext.AutoTextLabel

/**
 * Created by Faisal Amir on 11/03/23
 * https://github.com/amirisback
 */


class AutoTextKeyboardViewModel() {

    fun getAutoText(): List<AutoTextEntity> {
        val data = mutableListOf<AutoTextEntity>()
        for (i in 1..10) {
            data.add(
                AutoTextEntity(
                    id = i,
                    title = "Title $i",
                    label = AutoTextLabel.DEFAULT,
                    date = FrogoDate.getTimeNow(),
                    body = "Body $i",
                    isActive = true
                )
            )
        }
        return data
    }

}