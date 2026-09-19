package com.frogobox.appkeyboard.ui.autotext

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.frogobox.appkeyboard.common.base.BaseViewModel
import com.frogobox.appkeyboard.common.ext.getTimeNow
import com.frogobox.appkeyboard.model.AutoTextEntity
import com.frogobox.appkeyboard.model.AutoTextLabelType
import com.frogobox.appkeyboard.repository.autotext.AutoTextRepository
import com.frogobox.coresdk.source.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * Created by Faisal Amir on 11/03/23
 * https://github.com/amirisback
 */


@HiltViewModel
class AutoTextViewModel @Inject constructor(
    private val repository: AutoTextRepository,
) : BaseViewModel() {

    private var _autoText = MutableLiveData<Resource<List<AutoTextEntity>>>()
    var autoText: LiveData<Resource<List<AutoTextEntity>>> = _autoText

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
        viewModelScope.launch {
            repository.getAutoText()
                .onStart {
                    _autoText.postValue(Resource.Loading())
                }
                .catch { e ->
                    _autoText.postValue(Resource.Error(-1, e.localizedMessage ?: "Unknown Error"))
                }
                .collect { data ->
                    _autoText.postValue(Resource.Success(data))
                }
        }
    }

    fun insertAutoText(title: String, body: String) {
        val data = AutoTextEntity(
            title = title,
            label = AutoTextLabelType.DEFAULT,
            date = getTimeNow(),
            body = body,
            isActive = true
        )
        viewModelScope.launch {
            _eventShowProgressState.postValue(true)
            try {
                repository.insertAutoText(data)
                _eventShowProgressState.postValue(false)
                _eventSuccessState.postValue(true)
                _eventFinishState.postValue(true)
            } catch (e: Exception) {
                _eventShowProgressState.postValue(false)
                _eventFailed.postValue(e.localizedMessage ?: "Failed to insert AutoText")
                _eventFinishState.postValue(true)
            }
        }
    }

    fun deleteAutoText(data: AutoTextEntity) {
        viewModelScope.launch {
            _eventShowProgressState.postValue(true)
            try {
                repository.deleteAutoText(data)
                _eventShowProgressState.postValue(false)
                _eventSuccessState.postValue(true)
                _eventFinishState.postValue(true)
            } catch (e: Exception) {
                _eventShowProgressState.postValue(false)
                _eventFailed.postValue(e.localizedMessage ?: "Failed to delete AutoText")
                _eventFinishState.postValue(true)
            }
        }
    }

    fun updateAutoText(id: Int, title: String, body: String) {
        val data = AutoTextEntity(
            id = id,
            title = title,
            label = AutoTextLabelType.DEFAULT,
            date = getTimeNow(),
            body = body,
            isActive = true
        )
        viewModelScope.launch {
            _eventShowProgressState.postValue(true)
            try {
                repository.updateAutoText(data)
                _eventShowProgressState.postValue(false)
                _eventSuccessState.postValue(true)
                _eventFinishState.postValue(true)
            } catch (e: Exception) {
                _eventShowProgressState.postValue(false)
                _eventFailed.postValue(e.localizedMessage ?: "Failed to update AutoText")
                _eventFinishState.postValue(true)
            }
        }
    }

}