package com.frogobox.appkeyboard.di.hilt

import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

/**
 * Created by Faisal Amir on 06/01/23
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */

@Module(includes = [NetworkModule::class])
@InstallIn(SingletonComponent::class)
class ServiceModule {


}