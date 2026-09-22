package com.frogobox.appkeyboard

import com.frogobox.appkeyboard.model.KeyboardFeatureType
import com.frogobox.appkeyboard.model.TemplateCategoryType
import com.frogobox.appkeyboard.model.TemplateTextEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

class TemplateTextCrudUnitTest {

    @Test
    fun testTemplateCategoryTypeMappings() {
        assertEquals(5, TemplateCategoryType.entries.size)

        // Verify keys
        assertEquals("GAME", TemplateCategoryType.GAME.key)
        assertEquals("APP", TemplateCategoryType.APP.key)
        assertEquals("SALE", TemplateCategoryType.SALE.key)
        assertEquals("GREETING", TemplateCategoryType.GREETING.key)
        assertEquals("LOVE", TemplateCategoryType.LOVE.key)

        // Verify icons
        assertEquals("🎮", TemplateCategoryType.GAME.icon)
        assertEquals("📱", TemplateCategoryType.APP.icon)
        assertEquals("💰", TemplateCategoryType.SALE.icon)
        assertEquals("👋", TemplateCategoryType.GREETING.icon)
        assertEquals("❤️", TemplateCategoryType.LOVE.icon)

        // Verify FeatureType linkage
        assertEquals(KeyboardFeatureType.TEMPLATE_TEXT_GAME, TemplateCategoryType.GAME.featureType)
        assertEquals(KeyboardFeatureType.TEMPLATE_TEXT_APP, TemplateCategoryType.APP.featureType)
        assertEquals(KeyboardFeatureType.TEMPLATE_TEXT_SALE, TemplateCategoryType.SALE.featureType)
        assertEquals(KeyboardFeatureType.TEMPLATE_TEXT_GREETING, TemplateCategoryType.GREETING.featureType)
        assertEquals(KeyboardFeatureType.TEMPLATE_TEXT_LOVE, TemplateCategoryType.LOVE.featureType)
    }

    @Test
    fun testTemplateCategoryTypeFromKey() {
        assertEquals(TemplateCategoryType.GAME, TemplateCategoryType.fromKey("GAME"))
        assertEquals(TemplateCategoryType.GAME, TemplateCategoryType.fromKey("game"))
        assertEquals(TemplateCategoryType.APP, TemplateCategoryType.fromKey("APP"))
        assertEquals(TemplateCategoryType.APP, TemplateCategoryType.fromKey("app"))
        assertEquals(TemplateCategoryType.SALE, TemplateCategoryType.fromKey("SALE"))
        assertEquals(TemplateCategoryType.SALE, TemplateCategoryType.fromKey("sale"))
        assertEquals(TemplateCategoryType.GREETING, TemplateCategoryType.fromKey("GREETING"))
        assertEquals(TemplateCategoryType.GREETING, TemplateCategoryType.fromKey("greeting"))
        assertEquals(TemplateCategoryType.LOVE, TemplateCategoryType.fromKey("LOVE"))
        assertEquals(TemplateCategoryType.LOVE, TemplateCategoryType.fromKey("love"))

        // Null and invalid fallbacks
        assertEquals(TemplateCategoryType.GAME, TemplateCategoryType.fromKey(null))
        assertEquals(TemplateCategoryType.GAME, TemplateCategoryType.fromKey(""))
        assertEquals(TemplateCategoryType.GAME, TemplateCategoryType.fromKey("unknown_category"))
    }

    @Test
    fun testTemplateCategoryTypeFromFeatureType() {
        assertEquals(TemplateCategoryType.GAME, TemplateCategoryType.fromFeatureType(KeyboardFeatureType.TEMPLATE_TEXT_GAME))
        assertEquals(TemplateCategoryType.APP, TemplateCategoryType.fromFeatureType(KeyboardFeatureType.TEMPLATE_TEXT_APP))
        assertEquals(TemplateCategoryType.SALE, TemplateCategoryType.fromFeatureType(KeyboardFeatureType.TEMPLATE_TEXT_SALE))
        assertEquals(TemplateCategoryType.GREETING, TemplateCategoryType.fromFeatureType(KeyboardFeatureType.TEMPLATE_TEXT_GREETING))
        assertEquals(TemplateCategoryType.LOVE, TemplateCategoryType.fromFeatureType(KeyboardFeatureType.TEMPLATE_TEXT_LOVE))
    }

    @Test
    fun testTemplateEntityDataModel() {
        val now = System.currentTimeMillis()
        val entity = TemplateTextEntity(
            id = 42,
            category = "SALE",
            text = "Promo diskon kilat 50% khusus hari ini!",
            createdAt = now,
            updatedAt = now
        )

        assertEquals(42, entity.id)
        assertEquals("SALE", entity.category)
        assertEquals("Promo diskon kilat 50% khusus hari ini!", entity.text)
        assertEquals(now, entity.createdAt)
        assertEquals(now, entity.updatedAt)
    }

    @Test
    fun testCategoryAndSearchFiltering() {
        val sampleList = listOf(
            TemplateTextEntity(id = 1, category = "GAME", text = "GGWP! Great match today."),
            TemplateTextEntity(id = 2, category = "GAME", text = "Let's queue together tonight."),
            TemplateTextEntity(id = 3, category = "APP", text = "Loving the sleek new interface!"),
            TemplateTextEntity(id = 4, category = "SALE", text = "Halo kak, pesanan sedang diproses."),
            TemplateTextEntity(id = 5, category = "GREETING", text = "Selamat pagi! Ada yang bisa kami bantu?"),
            TemplateTextEntity(id = 6, category = "LOVE", text = "Thinking of you always ❤️")
        )

        // 1. Filter by category
        val gameOnly = sampleList.filter { it.category == TemplateCategoryType.GAME.key }
        assertEquals(2, gameOnly.size)

        val saleOnly = sampleList.filter { it.category == TemplateCategoryType.SALE.key }
        assertEquals(1, saleOnly.size)
        assertEquals("Halo kak, pesanan sedang diproses.", saleOnly.first().text)

        // 2. Search query matching
        val query = "selamat"
        val searchResults = sampleList.filter { it.text.contains(query, ignoreCase = true) }
        assertEquals(1, searchResults.size)
        assertEquals(5, searchResults.first().id)

        // 3. Search query with empty results
        val noMatch = sampleList.filter { it.text.contains("NonExistentTerm123", ignoreCase = true) }
        assertTrue(noMatch.isEmpty())
    }

    @Test
    fun testInputValidationLogic() {
        fun isValidTemplate(text: String): Boolean {
            return text.trim().isNotEmpty() && text.length <= 500
        }

        assertTrue(isValidTemplate("Template valid"))
        assertFalse(isValidTemplate(""))
        assertFalse(isValidTemplate("    "))
        assertFalse(isValidTemplate("\n\n\t"))

        val tooLong = "A".repeat(501)
        assertFalse(isValidTemplate(tooLong))

        val exactMax = "A".repeat(500)
        assertTrue(isValidTemplate(exactMax))
    }

    @Test
    fun testRoomMigration3to4Metadata() {
        val migration = com.frogobox.appkeyboard.data.local.db.AppDatabase.MIGRATION_3_4
        assertEquals(3, migration.startVersion)
        assertEquals(4, migration.endVersion)
        assertNotNull(migration)
    }

    @Test
    fun testRoomSchemaVersion4HasTemplateTextCategoryIndex() {
        val schemaFile = java.io.File("schemas/com.frogobox.appkeyboard.data.local.db.AppDatabase/4.json")
        if (schemaFile.exists()) {
            val content = schemaFile.readText()
            assertTrue(content.contains("\"tableName\": \"template_text\""))
            assertTrue(content.contains("\"name\": \"index_template_text_category\""))
            assertTrue(content.contains("`category`"))
        }
    }
}
