package com.frogobox.keyboard.ui.autotext

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.frogobox.coresdk.util.FrogoDate
import com.frogobox.keyboard.common.base.BaseViewModel
import com.frogobox.keyboard.data.local.autotext.AutoTextEntity
import com.frogobox.keyboard.data.local.autotext.AutoTextLabel
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

    private var _autoText = MutableLiveData<MutableList<AutoTextEntity>>()
    var autoText : LiveData<MutableList<AutoTextEntity>> = _autoText

    fun getAutoText() {
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
        _autoText.postValue(data)
    }


}