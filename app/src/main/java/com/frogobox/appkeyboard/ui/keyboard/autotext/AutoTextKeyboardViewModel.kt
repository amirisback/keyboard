package com.frogobox.appkeyboard.ui.keyboard.autotext

import android.content.Context
import com.frogobox.appkeyboard.data.local.db.AppDatabase
import com.frogobox.appkeyboard.model.AutoTextEntity
import com.frogobox.appkeyboard.repository.autotext.AutoTextRepository
import com.frogobox.appkeyboard.repository.autotext.AutoTextRepositoryImpl
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Created by Faisal Amir on 11/03/23
 * https://github.com/amirisback
 */

class AutoTextKeyboardViewModel(val context: Context) {

    private fun getRepository(): AutoTextRepository {
        return AutoTextRepositoryImpl(AppDatabase.newInstance(context).autoTextDao())
    }

    fun getAutoTextFlow(): Flow<List<AutoTextEntity>> {
        return getRepository().getAutoText()
    }

    fun getAutoText(onSuccessData: (List<AutoTextEntity>) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                getRepository().getAutoText().collect { data ->
                    withContext(Dispatchers.Main) {
                        onSuccessData(data)
                    }
                }
            } catch (_: Exception) {
            }
        }
    }

}