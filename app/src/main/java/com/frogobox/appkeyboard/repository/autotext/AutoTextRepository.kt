package com.frogobox.appkeyboard.repository.autotext

import com.frogobox.appkeyboard.common.callback.DataResponseCallback
import com.frogobox.appkeyboard.common.callback.StateResponseCallback
import com.frogobox.appkeyboard.model.AutoTextEntity
import com.frogobox.appkeyboard.model.AutoTextLabelType
import kotlinx.coroutines.flow.Flow

/**
 * Created by Faisal Amir on 25/11/22
 * https://github.com/amirisback
 */

interface AutoTextRepository {

    fun getAutoText(): Flow<List<AutoTextEntity>>

    fun getAutoTextByLabel(label: AutoTextLabelType): Flow<List<AutoTextEntity>>

    fun getAutoTextByTitle(title: String): Flow<List<AutoTextEntity>>

    fun getAutoTextByBody(body: String): Flow<List<AutoTextEntity>>

    fun getAutoTextByTitleOrBody(keyword: String): Flow<List<AutoTextEntity>>

    suspend fun insertAutoText(autoText: AutoTextEntity)

    suspend fun insertAutoText(autoTexts: List<AutoTextEntity>)

    suspend fun updateAutoText(autoText: AutoTextEntity)

    suspend fun deleteAutoText(autoText: AutoTextEntity)

    suspend fun deleteAutoText(idList: List<Int>)

    suspend fun nukeAutoText()

    fun getAutoText(callback: DataResponseCallback<List<AutoTextEntity>>)

    fun getAutoTextByLabel(label: AutoTextLabelType, callback: DataResponseCallback<List<AutoTextEntity>>)

    fun getAutoTextByTitle(title: String, callback: DataResponseCallback<List<AutoTextEntity>>)

    fun getAutoTextByBody(body: String, callback: DataResponseCallback<List<AutoTextEntity>>)

    fun getAutoTextByTitleOrBody(keyword: String, callback: DataResponseCallback<List<AutoTextEntity>>)

    fun insertAutoText(autoText: AutoTextEntity, callback: StateResponseCallback)

    fun insertAutoText(autoTexts: List<AutoTextEntity>, callback: StateResponseCallback)

    fun updateAutoText(autoText: AutoTextEntity, callback: StateResponseCallback)

    fun deleteAutoText(autoText: AutoTextEntity, callback: StateResponseCallback)

    fun deleteAutoText(idList: List<Int>, callback: StateResponseCallback)

    fun nukeAutoText(callback: StateResponseCallback)

}