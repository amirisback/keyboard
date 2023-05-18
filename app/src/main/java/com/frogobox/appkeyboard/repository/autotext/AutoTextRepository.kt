package com.frogobox.appkeyboard.repository.autotext

import com.frogobox.appkeyboard.common.callback.DataResponseCallback
import com.frogobox.appkeyboard.common.callback.StateResponseCallback
import com.frogobox.appkeyboard.model.AutoTextEntity
import com.frogobox.appkeyboard.model.AutoTextLabel


/**
 * Created by Faisal Amir on 25/11/22
 * https://github.com/amirisback
 */

interface AutoTextRepository {

    fun getAutoText(callback: DataResponseCallback<List<AutoTextEntity>>)

    fun getAutoTextByLabel(label: AutoTextLabel, callback: DataResponseCallback<List<AutoTextEntity>>)

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