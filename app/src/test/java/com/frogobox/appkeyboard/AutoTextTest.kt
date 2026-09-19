package com.frogobox.appkeyboard

import com.frogobox.appkeyboard.model.AutoTextEntity
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AutoTextTest {

    private val sampleList = listOf(
        AutoTextEntity(id = 1, title = "Greeting Morning", body = "Good morning! How can I help you today?"),
        AutoTextEntity(id = 2, title = "Bank Account", body = "BCA: 1234567890 a/n Faisal Amir"),
        AutoTextEntity(id = 3, title = "Email Address", body = "faisal.amir@example.com"),
        AutoTextEntity(id = 4, title = "Closing Note", body = "Thank you for reaching out. Have a wonderful day!")
    )

    @Test
    fun testAutoTextFilteringByTitle() {
        val query = "bank"
        val filtered = sampleList.filter {
            it.title.contains(query, ignoreCase = true) || it.body.contains(query, ignoreCase = true)
        }
        assertEquals(1, filtered.size)
        assertEquals("Bank Account", filtered[0].title)
    }

    @Test
    fun testAutoTextFilteringByBody() {
        val query = "1234567890"
        val filtered = sampleList.filter {
            it.title.contains(query, ignoreCase = true) || it.body.contains(query, ignoreCase = true)
        }
        assertEquals(1, filtered.size)
        assertEquals("Bank Account", filtered[0].title)
    }

    @Test
    fun testAutoTextFilteringCaseInsensitive() {
        val query = "GREETING"
        val filtered = sampleList.filter {
            it.title.contains(query, ignoreCase = true) || it.body.contains(query, ignoreCase = true)
        }
        assertEquals(1, filtered.size)
        assertEquals(1, filtered[0].id)
    }

    @Test
    fun testAutoTextFilteringEmptyQueryReturnsAll() {
        val query = ""
        val filtered = sampleList.filter {
            it.title.contains(query, ignoreCase = true) || it.body.contains(query, ignoreCase = true)
        }
        assertEquals(sampleList.size, filtered.size)
    }

    @Test
    fun testAutoTextFilteringNoMatch() {
        val query = "xyznonexistent"
        val filtered = sampleList.filter {
            it.title.contains(query, ignoreCase = true) || it.body.contains(query, ignoreCase = true)
        }
        assertTrue(filtered.isEmpty())
    }

    @Test
    fun testAutoTextWordCountCalculation() {
        val text = "Good morning! How can I help you today?"
        val wordCount = text.trim().split(Regex("\\s+")).count { it.isNotEmpty() }
        assertEquals(8, wordCount)

        val emptyText = "   "
        val emptyWordCount = if (emptyText.isBlank()) 0 else emptyText.trim().split(Regex("\\s+")).count { it.isNotEmpty() }
        assertEquals(0, emptyWordCount)
    }

    @Test
    fun testAutoTextCharacterCount() {
        val body = "Hello World"
        assertEquals(11, body.length)
    }

    @Test
    fun testAutoTextFormValidation() {
        val validTitle = "My Title"
        val validBody = "My Body"
        assertTrue(validTitle.isNotBlank() && validBody.isNotBlank())

        val emptyTitle = ""
        assertFalse(emptyTitle.isNotBlank() && validBody.isNotBlank())

        val blankBody = "   "
        assertFalse(validTitle.isNotBlank() && blankBody.isNotBlank())
    }
}
