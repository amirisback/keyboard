package com.frogobox.appkeyboard.ui.keyboard.templatetext

import android.content.Context
import com.frogobox.appkeyboard.model.KeyboardFeatureType
import com.frogobox.appkeyboard.model.TemplateText
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
            TemplateText(index, s, KeyboardFeatureType.TEMPLATE_TEXT_APP)
        }.shuffled()
    }

    fun getTextGame(context: Context): List<TemplateText> {
        return getDataAsset(context, "text/text_game.json").mapIndexed { index, s ->
            TemplateText(index, s, KeyboardFeatureType.TEMPLATE_TEXT_GAME)
        }.shuffled()
    }

    fun getTextSale(context: Context): List<TemplateText> {
        return getDataAsset(context, "text/text_sale.json").mapIndexed { index, s ->
            TemplateText(index, s, KeyboardFeatureType.TEMPLATE_TEXT_SALE)
        }.shuffled()
    }

    fun getTextLove(context: Context): List<TemplateText> {
        return getDataAsset(context, "text/text_love.json").mapIndexed { index, s ->
            TemplateText(index, s, KeyboardFeatureType.TEMPLATE_TEXT_LOVE)
        }.shuffled()
    }

    fun getTextGreeting(context: Context): List<TemplateText> {
        return getDataAsset(context, "text/text_greeting.json").mapIndexed { index, s ->
            TemplateText(index, s, KeyboardFeatureType.TEMPLATE_TEXT_GREETING)
        }.shuffled()
    }

}