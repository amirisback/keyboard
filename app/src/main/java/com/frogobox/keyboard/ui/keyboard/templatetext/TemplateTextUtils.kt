package com.frogobox.keyboard.ui.keyboard.templatetext

import android.content.Context
import com.frogobox.keyboard.model.TemplateText
import com.frogobox.keyboard.model.TemplateTextType
import com.frogobox.sdk.util.FrogoFunc

/**
 * Created by Faisal Amir on 24/10/22
 * -----------------------------------------
 * E-mail   : faisalamircs@gmail.com
 * Github   : github.com/amirisback
 * -----------------------------------------
 * Copyright (C) Frogobox ID / amirisback
 * All rights reserved
 */


object TemplateTextUtils {

    private fun getDataAsset(context: Context, fileName: String): List<String> {
        return FrogoFunc.getArrayFromJsonAsset(context, fileName)
    }

    fun getTextApp(context: Context): List<TemplateText> {
        return getDataAsset(context, "text/text_app.json").mapIndexed { index, s ->
            TemplateText(index, s, TemplateTextType.APP)
        }.shuffled()
    }

    fun getTextGame(context: Context): List<TemplateText> {
        return getDataAsset(context, "text/text_game.json").mapIndexed { index, s ->
            TemplateText(index, s, TemplateTextType.GAME)
        }.shuffled()
    }

    fun getTextSales(context: Context): List<TemplateText> {
        return getDataAsset(context, "text/text_sales.json").mapIndexed { index, s ->
            TemplateText(index, s, TemplateTextType.SALE)
        }.shuffled()
    }

    fun getTextGreeting(context: Context): List<TemplateText> {
        return getDataAsset(context, "text/text_greeting.json").mapIndexed { index, s ->
            TemplateText(index, s, TemplateTextType.GREETING)
        }.shuffled()
    }

}