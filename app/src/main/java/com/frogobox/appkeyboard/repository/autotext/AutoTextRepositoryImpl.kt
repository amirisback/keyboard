package com.frogobox.appkeyboard.repository.autotext

import com.frogobox.appkeyboard.common.callback.DataResponseCallback
import com.frogobox.appkeyboard.common.callback.StateResponseCallback
import com.frogobox.appkeyboard.data.local.autotext.AutoTextDao
import com.frogobox.appkeyboard.model.AutoTextEntity
import com.frogobox.appkeyboard.model.AutoTextLabelType
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

/**
 * Created by Faisal Amir on 10/03/23
 * https://github.com/amirisback
 */

class AutoTextRepositoryImpl @Inject constructor(
    private val dao: AutoTextDao,
) : AutoTextRepository {

    override fun getAutoText(): Flow<List<AutoTextEntity>> {
        return dao.getAll().flowOn(Dispatchers.IO)
    }

    override fun getAutoTextByLabel(label: AutoTextLabelType): Flow<List<AutoTextEntity>> {
        return dao.getByLabel(label).flowOn(Dispatchers.IO)
    }

    override fun getAutoTextByTitle(title: String): Flow<List<AutoTextEntity>> {
        return dao.getByTitle(title).flowOn(Dispatchers.IO)
    }

    override fun getAutoTextByBody(body: String): Flow<List<AutoTextEntity>> {
        return dao.getByBody(body).flowOn(Dispatchers.IO)
    }

    override fun getAutoTextByTitleOrBody(keyword: String): Flow<List<AutoTextEntity>> {
        return dao.getByTitleOrBody(keyword).flowOn(Dispatchers.IO)
    }

    override suspend fun insertAutoText(autoText: AutoTextEntity) {
        withContext(Dispatchers.IO) {
            dao.insert(autoText)
        }
    }

    override suspend fun insertAutoText(autoTexts: List<AutoTextEntity>) {
        withContext(Dispatchers.IO) {
            dao.insert(autoTexts)
        }
    }

    override suspend fun updateAutoText(autoText: AutoTextEntity) {
        withContext(Dispatchers.IO) {
            dao.update(autoText)
        }
    }

    override suspend fun deleteAutoText(autoText: AutoTextEntity) {
        withContext(Dispatchers.IO) {
            dao.delete(autoText)
        }
    }

    override suspend fun deleteAutoText(idList: List<Int>) {
        withContext(Dispatchers.IO) {
            dao.delete(idList)
        }
    }

    override suspend fun nukeAutoText() {
        withContext(Dispatchers.IO) {
            dao.nukeData()
        }
    }

    override fun getAutoText(callback: DataResponseCallback<List<AutoTextEntity>>) {
        callback.onShowProgress()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                dao.getAll().collect {
                    withContext(Dispatchers.Main) {
                        callback.onHideProgress()
                        callback.onSuccess(it)
                        callback.onFinish()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onHideProgress()
                    callback.onFailed(-1, e.localizedMessage ?: "Unknown Error")
                    callback.onFinish()
                }
            }
        }
    }

    override fun getAutoTextByLabel(
        label: AutoTextLabelType,
        callback: DataResponseCallback<List<AutoTextEntity>>,
    ) {
        callback.onShowProgress()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                dao.getByLabel(label).collect {
                    withContext(Dispatchers.Main) {
                        callback.onHideProgress()
                        callback.onSuccess(it)
                        callback.onFinish()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onHideProgress()
                    callback.onFailed(-1, e.localizedMessage ?: "Unknown Error")
                    callback.onFinish()
                }
            }
        }
    }

    override fun getAutoTextByTitle(
        title: String,
        callback: DataResponseCallback<List<AutoTextEntity>>,
    ) {
        callback.onShowProgress()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                dao.getByTitle(title).collect {
                    withContext(Dispatchers.Main) {
                        callback.onHideProgress()
                        callback.onSuccess(it)
                        callback.onFinish()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onHideProgress()
                    callback.onFailed(-1, e.localizedMessage ?: "Unknown Error")
                    callback.onFinish()
                }
            }
        }
    }

    override fun getAutoTextByBody(
        body: String,
        callback: DataResponseCallback<List<AutoTextEntity>>,
    ) {
        callback.onShowProgress()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                dao.getByBody(body).collect {
                    withContext(Dispatchers.Main) {
                        callback.onHideProgress()
                        callback.onSuccess(it)
                        callback.onFinish()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onHideProgress()
                    callback.onFailed(-1, e.localizedMessage ?: "Unknown Error")
                    callback.onFinish()
                }
            }
        }
    }

    override fun getAutoTextByTitleOrBody(
        keyword: String,
        callback: DataResponseCallback<List<AutoTextEntity>>,
    ) {
        callback.onShowProgress()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                dao.getByTitleOrBody(keyword).collect {
                    withContext(Dispatchers.Main) {
                        callback.onHideProgress()
                        callback.onSuccess(it)
                        callback.onFinish()
                    }
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onHideProgress()
                    callback.onFailed(-1, e.localizedMessage ?: "Unknown Error")
                    callback.onFinish()
                }
            }
        }
    }

    override fun insertAutoText(autoText: AutoTextEntity, callback: StateResponseCallback) {
        callback.onShowProgress()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                dao.insert(autoText)
                withContext(Dispatchers.Main) {
                    callback.onHideProgress()
                    callback.onSuccess()
                    callback.onFinish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onHideProgress()
                    callback.onFailed(-1, e.localizedMessage ?: "Unknown Error")
                    callback.onFinish()
                }
            }
        }
    }

    override fun insertAutoText(autoTexts: List<AutoTextEntity>, callback: StateResponseCallback) {
        callback.onShowProgress()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                dao.insert(autoTexts)
                withContext(Dispatchers.Main) {
                    callback.onHideProgress()
                    callback.onSuccess()
                    callback.onFinish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onHideProgress()
                    callback.onFailed(-1, e.localizedMessage ?: "Unknown Error")
                    callback.onFinish()
                }
            }
        }
    }

    override fun updateAutoText(autoText: AutoTextEntity, callback: StateResponseCallback) {
        callback.onShowProgress()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                dao.update(autoText)
                withContext(Dispatchers.Main) {
                    callback.onHideProgress()
                    callback.onSuccess()
                    callback.onFinish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onHideProgress()
                    callback.onFailed(-1, e.localizedMessage ?: "Unknown Error")
                    callback.onFinish()
                }
            }
        }
    }

    override fun deleteAutoText(autoText: AutoTextEntity, callback: StateResponseCallback) {
        callback.onShowProgress()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                dao.delete(autoText)
                withContext(Dispatchers.Main) {
                    callback.onHideProgress()
                    callback.onSuccess()
                    callback.onFinish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onHideProgress()
                    callback.onFailed(-1, e.localizedMessage ?: "Unknown Error")
                    callback.onFinish()
                }
            }
        }
    }

    override fun deleteAutoText(idList: List<Int>, callback: StateResponseCallback) {
        callback.onShowProgress()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                dao.delete(idList)
                withContext(Dispatchers.Main) {
                    callback.onHideProgress()
                    callback.onSuccess()
                    callback.onFinish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onHideProgress()
                    callback.onFailed(-1, e.localizedMessage ?: "Unknown Error")
                    callback.onFinish()
                }
            }
        }
    }

    override fun nukeAutoText(callback: StateResponseCallback) {
        callback.onShowProgress()
        CoroutineScope(Dispatchers.IO).launch {
            try {
                dao.nukeData()
                withContext(Dispatchers.Main) {
                    callback.onHideProgress()
                    callback.onSuccess()
                    callback.onFinish()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    callback.onHideProgress()
                    callback.onFailed(-1, e.localizedMessage ?: "Unknown Error")
                    callback.onFinish()
                }
            }
        }
    }

}