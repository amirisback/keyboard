package com.frogobox.appkeyboard.ui.autotext

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.frogobox.appkeyboard.common.base.BaseViewModel
import com.frogobox.appkeyboard.common.callback.DataResponseCallback
import com.frogobox.appkeyboard.common.callback.StateResponseCallback
import com.frogobox.appkeyboard.model.AutoTextEntity
import com.frogobox.appkeyboard.model.AutoTextLabel
import com.frogobox.appkeyboard.repository.autotext.AutoTextRepository
import com.frogobox.coresdk.util.FrogoDate
import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject

/**
 * Created by Faisal Amir on 11/03/23
 * https://github.com/amirisback
 */


@HiltViewModel
class AutoTextViewModel @Inject constructor(
    private val repository: AutoTextRepository,
) : BaseViewModel() {

    private var _autoText = MutableLiveData<List<AutoTextEntity>>()
    var autoText: LiveData<List<AutoTextEntity>> = _autoText

    fun getAutoText() {
        repository.getAutoText(object : DataResponseCallback<List<AutoTextEntity>> {
            override fun onFailed(statusCode: Int, errorMessage: String) {
                _eventFailed.postValue(errorMessage)
            }

            override fun onFinish() {
                _eventFinishState.postValue(true)
            }

            override fun onHideProgress() {
                _eventShowProgressState.postValue(false)

            }

            override fun onShowProgress() {
                _eventShowProgressState.postValue(true)

            }

            override fun onSuccess(data: List<AutoTextEntity>) {
                _autoText.postValue(data)
            }

        })
    }

    fun insertAutoText(title: String, body: String) {
        val data = AutoTextEntity(
            title = title,
            label = AutoTextLabel.DEFAULT,
            date = FrogoDate.getTimeNow(),
            body = body,
            isActive = true
        )
        repository.insertAutoText(data, object : StateResponseCallback {
            override fun onFailed(statusCode: Int, errorMessage: String) {
                _eventFailed.postValue(errorMessage)
            }

            override fun onFinish() {
                _eventFinishState.postValue(true)
            }

            override fun onHideProgress() {
                _eventShowProgressState.postValue(false)

            }

            override fun onShowProgress() {
                _eventShowProgressState.postValue(true)

            }

            override fun onSuccess() {
                _eventSuccessState.postValue(true)
            }
        }
        )
    }

    fun deleteAutoText(data: AutoTextEntity) {
        repository.deleteAutoText(data, object : StateResponseCallback {
            override fun onFailed(statusCode: Int, errorMessage: String) {
                _eventFailed.postValue(errorMessage)
            }

            override fun onFinish() {
                _eventFinishState.postValue(true)
            }

            override fun onHideProgress() {
                _eventShowProgressState.postValue(false)
            }

            override fun onShowProgress() {
                _eventShowProgressState.postValue(true)
            }

            override fun onSuccess() {
                _eventSuccessState.postValue(true)
            }
        })
    }

    fun updateAutoText(id: Int, title: String, body: String) {
        val data = AutoTextEntity(
            id = id,
            title = title,
            label = AutoTextLabel.DEFAULT,
            date = FrogoDate.getTimeNow(),
            body = body,
            isActive = true
        )
        repository.updateAutoText(data, object : StateResponseCallback {
            override fun onFailed(statusCode: Int, errorMessage: String) {
                _eventFailed.postValue(errorMessage)
            }

            override fun onFinish() {
                _eventFinishState.postValue(true)
            }

            override fun onHideProgress() {
                _eventShowProgressState.postValue(false)
            }

            override fun onShowProgress() {
                _eventShowProgressState.postValue(true)
            }

            override fun onSuccess() {
                _eventSuccessState.postValue(true)
            }
        })
    }

}