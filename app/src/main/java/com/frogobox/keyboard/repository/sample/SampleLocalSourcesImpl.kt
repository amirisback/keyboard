package com.frogobox.research.repository.sample

import com.frogobox.keyboard.data.local.sample.SampleDao
import com.frogobox.research.repository.DataSourcesImpl
import javax.inject.Inject

/**
 * Created by Faisal Amir on 06/01/23
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */


class SampleLocalSourcesImpl @Inject constructor(
    private val dao: SampleDao
) : DataSourcesImpl(), SampleLocalSources {

    override fun getSampleLocalData(): String {
        return "Sample Local Data"
    }


}