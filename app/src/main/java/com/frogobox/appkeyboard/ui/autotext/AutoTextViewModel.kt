package com.frogobox.appkeyboard.ui.autotext

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.frogobox.appkeyboard.common.base.BaseViewModel
import com.frogobox.appkeyboard.common.callback.DataResponseCallback
import com.frogobox.appkeyboard.common.callback.StateResponseCallback
import com.frogobox.appkeyboard.model.AutoTextEntity
import com.frogobox.appkeyboard.model.AutoTextLabelType
import com.frogobox.appkeyboard.repository.autotext.AutoTextRepository
import com.frogobox.coresdk.source.FrogoResult
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

    private var _autoText = MutableLiveData<FrogoResult<List<AutoTextEntity>>>()
    var autoText: LiveData<FrogoResult<List<AutoTextEntity>>> = _autoText

    protected var _eventFailed = MutableLiveData<String>()
    var eventFailed: LiveData<String> = _eventFailed

    protected var _eventSuccess = MutableLiveData<String>()
    var eventSuccess: LiveData<String> = _eventSuccess

    protected var _eventEmptyState = MutableLiveData<Boolean>()
    var eventEmptyState: LiveData<Boolean> = _eventEmptyState

    protected var _eventFailedState = MutableLiveData<Boolean>()
    var eventFailedState: LiveData<Boolean> = _eventFailedState

    protected var _eventFinishState = MutableLiveData<Boolean>()
    var eventFinishState: LiveData<Boolean> = _eventFinishState

    protected var _eventSuccessState = MutableLiveData<Boolean>()
    var eventSuccessState: LiveData<Boolean> = _eventSuccessState

    protected var _eventNoInternetState = MutableLiveData<Boolean>()
    var eventNoInternetState: LiveData<Boolean> = _eventNoInternetState

    protected var _eventShowProgressState = MutableLiveData<Boolean>()
    var eventShowProgressState: LiveData<Boolean> = _eventShowProgressState


    fun getAutoText() {
        repository.getAutoText(object : DataResponseCallback<List<AutoTextEntity>> {
            override fun onFinish() {}
            override fun onHideProgress() {}

            override fun onFailed(statusCode: Int, errorMessage: String) {
                _autoText.postValue(FrogoResult.Error(statusCode, errorMessage))
            }

            override fun onShowProgress() {
                _autoText.postValue(FrogoResult.Loading(true))
            }

            override fun onSuccess(data: List<AutoTextEntity>) {
                _autoText.postValue(FrogoResult.Success(data))
            }

        })
    }

    fun insertAutoText(title: String, body: String) {
        val data = AutoTextEntity(
            title = title,
            label = AutoTextLabelType.DEFAULT,
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
            label = AutoTextLabelType.DEFAULT,
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