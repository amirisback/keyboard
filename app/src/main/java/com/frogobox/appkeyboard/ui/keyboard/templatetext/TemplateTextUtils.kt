package com.frogobox.appkeyboard.ui.keyboard.templatetext

import android.content.Context
import com.frogobox.appkeyboard.data.local.db.AppDatabase
import com.frogobox.appkeyboard.model.KeyboardFeatureType
import com.frogobox.appkeyboard.model.TemplateCategoryType
import com.frogobox.appkeyboard.model.TemplateText
import com.frogobox.appkeyboard.model.TemplateTextEntity
import com.frogobox.sdk.ext.getDataFromJsonAsset
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking

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
        return context.getDataFromJsonAsset(fileName)
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

    fun getTemplatesForType(context: Context, type: KeyboardFeatureType): List<TemplateText> {
        val categoryKey = when (type) {
            KeyboardFeatureType.TEMPLATE_TEXT_GAME -> TemplateCategoryType.GAME.key
            KeyboardFeatureType.TEMPLATE_TEXT_APP -> TemplateCategoryType.APP.key
            KeyboardFeatureType.TEMPLATE_TEXT_SALE -> TemplateCategoryType.SALE.key
            KeyboardFeatureType.TEMPLATE_TEXT_GREETING -> TemplateCategoryType.GREETING.key
            KeyboardFeatureType.TEMPLATE_TEXT_LOVE -> TemplateCategoryType.LOVE.key
            else -> return emptyList()
        }

        return try {
            val db = AppDatabase.newInstance(context)
            val dao = db.templateTextDao()
            val entities = runBlocking(Dispatchers.IO) {
                var items = dao.getByCategory(categoryKey)
                if (items.isEmpty()) {
                    val catType = TemplateCategoryType.fromFeatureType(type)
                    val assetFile = when (catType) {
                        TemplateCategoryType.GAME -> "text/text_game.json"
                        TemplateCategoryType.APP -> "text/text_app.json"
                        TemplateCategoryType.SALE -> "text/text_sale.json"
                        TemplateCategoryType.GREETING -> "text/text_greeting.json"
                        TemplateCategoryType.LOVE -> "text/text_love.json"
                    }
                    if (assetFile.isNotEmpty()) {
                        val strings = getDataAsset(context, assetFile)
                        val now = System.currentTimeMillis()
                        val list = strings.map { str ->
                            TemplateTextEntity(
                                category = categoryKey,
                                text = str,
                                createdAt = now,
                                updatedAt = now
                            )
                        }
                        dao.insertAll(list)
                        items = dao.getByCategory(categoryKey)
                    }
                }
                items
            }

            if (entities.isNotEmpty()) {
                entities.map { entity ->
                    TemplateText(entity.id, entity.text, type)
                }
            } else {
                getDefaultFallback(context, type)
            }
        } catch (_: Exception) {
            getDefaultFallback(context, type)
        }
    }

    private fun getDefaultFallback(context: Context, type: KeyboardFeatureType): List<TemplateText> {
        return when (type) {
            KeyboardFeatureType.TEMPLATE_TEXT_GAME -> getTextGame(context)
            KeyboardFeatureType.TEMPLATE_TEXT_APP -> getTextApp(context)
            KeyboardFeatureType.TEMPLATE_TEXT_SALE -> getTextSale(context)
            KeyboardFeatureType.TEMPLATE_TEXT_GREETING -> getTextGreeting(context)
            KeyboardFeatureType.TEMPLATE_TEXT_LOVE -> getTextLove(context)
            else -> emptyList()
        }
    }

}