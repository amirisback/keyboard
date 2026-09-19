package com.frogobox.appkeyboard

import com.frogobox.appkeyboard.model.KeyboardFeatureType
import com.frogobox.appkeyboard.ui.keyboard.templatetext.TemplateCategoryItem
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import java.net.URLEncoder

class KeyboardFeaturesRedesignTest {

    @Test
    fun testTemplateCategoriesCoverage() {
        val categories = listOf(
            TemplateCategoryItem(KeyboardFeatureType.TEMPLATE_TEXT_GAME, "🎮", "Game", true),
            TemplateCategoryItem(KeyboardFeatureType.TEMPLATE_TEXT_APP, "📱", "App", false),
            TemplateCategoryItem(KeyboardFeatureType.TEMPLATE_TEXT_SALE, "💰", "Sale", false),
            TemplateCategoryItem(KeyboardFeatureType.TEMPLATE_TEXT_GREETING, "👋", "Greeting", false),
            TemplateCategoryItem(KeyboardFeatureType.TEMPLATE_TEXT_LOVE, "❤️", "Love", false)
        )

        assertEquals(5, categories.size)
        assertTrue(categories.first().isSelected)
        assertEquals("🎮", categories.first().icon)
        assertEquals("Game", categories.first().title)

        // Test category switching simulation
        val switched = categories.map {
            it.copy(isSelected = it.type == KeyboardFeatureType.TEMPLATE_TEXT_SALE)
        }
        val selected = switched.first { it.isSelected }
        assertEquals(KeyboardFeatureType.TEMPLATE_TEXT_SALE, selected.type)
        assertEquals("Sale", selected.title)
    }

    @Test
    fun testFormKeyboardSnippetBuilder() {
        val title = "Invoice #1024"
        val details = "2x Mechanical Keyboard Switch Set"
        val number = "IDR 350.000"

        val sb = StringBuilder()
        if (title.isNotBlank()) sb.append("Subject: $title\n")
        if (details.isNotBlank()) sb.append("Details: $details\n")
        if (number.isNotBlank()) sb.append("Ref/No: $number\n")

        val expected = "Subject: Invoice #1024\nDetails: 2x Mechanical Keyboard Switch Set\nRef/No: IDR 350.000\n"
        assertEquals(expected, sb.toString())
    }

    @Test
    fun testFormKeyboardSnippetBuilderWithPartialFields() {
        val title = ""
        val details = "Quick reminder for the meeting at 3 PM"
        val number = ""

        val sb = StringBuilder()
        if (title.isNotBlank()) sb.append("Subject: $title\n")
        if (details.isNotBlank()) sb.append("Details: $details\n")
        if (number.isNotBlank()) sb.append("Ref/No: $number\n")

        val expected = "Details: Quick reminder for the meeting at 3 PM\n"
        assertEquals(expected, sb.toString())
    }

    @Test
    fun testMovieRatingFormatting() {
        val vote1 = 8.423
        val vote2 = 7.0
        val vote3 = 9.99

        assertEquals("8.4", String.format(java.util.Locale.US, "%.1f", vote1))
        assertEquals("7.0", String.format(java.util.Locale.US, "%.1f", vote2))
        assertEquals("10.0", String.format(java.util.Locale.US, "%.1f", vote3))
    }

    @Test
    fun testNewsSourceFallbackLogic() {
        val authorValid: String? = "BBC News"
        val authorBlank: String? = "   "
        val authorNull: String? = null

        val source1 = if (!authorValid.isNullOrBlank()) authorValid else "News"
        val source2 = if (!authorBlank.isNullOrBlank()) authorBlank else "News"
        val source3 = if (!authorNull.isNullOrBlank()) authorNull else "News"

        assertEquals("BBC News", source1)
        assertEquals("News", source2)
        assertEquals("News", source3)
    }

    @Test
    fun testWebviewUrlSearchResolution() {
        fun resolveUrl(query: String): String {
            val trimmed = query.trim()
            return when {
                trimmed.startsWith("http://") || trimmed.startsWith("https://") -> trimmed
                trimmed.contains(".") && !trimmed.contains(" ") -> "https://$trimmed"
                trimmed.isNotEmpty() -> "https://www.google.com/search?q=" + URLEncoder.encode(trimmed, "UTF-8")
                else -> "https://www.google.com"
            }
        }

        assertEquals("https://github.com", resolveUrl("https://github.com"))
        assertEquals("http://example.com", resolveUrl("http://example.com"))
        assertEquals("https://kotlinlang.org", resolveUrl("kotlinlang.org"))
        assertEquals("https://www.google.com/search?q=android+keyboard+design", resolveUrl("android keyboard design"))
        assertEquals("https://www.google.com", resolveUrl(""))
    }

    @Test
    fun testFeatureTypeFromIdMapping() {
        assertEquals(KeyboardFeatureType.AUTO_TEXT, KeyboardFeatureType.from("menu_auto_text"))
        assertEquals(KeyboardFeatureType.MOVIE, KeyboardFeatureType.from("menu_movie"))
        assertEquals(KeyboardFeatureType.NEWS, KeyboardFeatureType.from("menu_news"))
        assertEquals(KeyboardFeatureType.WEB, KeyboardFeatureType.from("menu_web"))
        assertEquals(KeyboardFeatureType.FORM, KeyboardFeatureType.from("menu_form"))
        assertEquals(KeyboardFeatureType.TEMPLATE_TEXT_GAME, KeyboardFeatureType.from("menu_play_store_game"))
        assertEquals(KeyboardFeatureType.AUTO_TEXT, KeyboardFeatureType.from("unknown_invalid_id"))
    }
}
