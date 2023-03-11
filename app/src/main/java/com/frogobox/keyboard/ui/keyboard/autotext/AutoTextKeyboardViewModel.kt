package com.frogobox.keyboard.ui.keyboard.autotext

import android.content.Context
import com.frogobox.coresdk.util.FrogoDate
import com.frogobox.keyboard.common.callback.DataResponseCallback
import com.frogobox.keyboard.data.local.autotext.AutoTextEntity
import com.frogobox.keyboard.data.local.autotext.AutoTextLabel
import com.frogobox.keyboard.data.local.db.AppDatabase
import com.frogobox.keyboard.repository.autotext.AutoTextRepository
import com.frogobox.keyboard.repository.autotext.AutoTextRepositoryImpl

/**
 * Created by Faisal Amir on 11/03/23
 * https://github.com/amirisback
 */


class AutoTextKeyboardViewModel(val context: Context) {

    private fun getRepository() : AutoTextRepository {
        return AutoTextRepositoryImpl(AppDatabase.newInstance(context).autoTextDao())
    }

    fun getAutoText(onSuccessData: (List<AutoTextEntity>) -> Unit) {
        getRepository().getAutoText(object : DataResponseCallback<List<AutoTextEntity>> {
            override fun onFailed(statusCode: Int, errorMessage: String) {}
            override fun onFinish() {}
            override fun onHideProgress() {}
            override fun onShowProgress() {}
            override fun onSuccess(data: List<AutoTextEntity>) {
                onSuccessData(data)
            }
        })
    }

}