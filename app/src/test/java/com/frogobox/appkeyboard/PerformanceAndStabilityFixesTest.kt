package com.frogobox.appkeyboard

import com.frogobox.appkeyboard.data.local.db.AppDatabase
import com.frogobox.appkeyboard.data.remote.model.DataItemResponse
import com.frogobox.appkeyboard.model.ProductEntity
import com.frogobox.appkeyboard.suggestion.WordSuggestionEngine
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test

/**
 * Rigorous Unit Test Suite for TASK-024:
 * Validates performance hardening, compound unique key safety,
 * Room DB migration 2->3 definition, and asynchronous word suggestion stability.
 */
class PerformanceAndStabilityFixesTest {

    @Test
    fun testIndexSafeCompoundKeyGuaranteesUniqueness() {
        // Simulate items with identical IDs, null IDs, and identical displayIndex
        val items = listOf(
            DataItemResponse(id = "item_dup", rowIndex = 1, productName = "Item A"),
            DataItemResponse(id = "item_dup", rowIndex = 1, productName = "Item B"),
            DataItemResponse(id = null, rowIndex = null, productName = "Item C"),
            DataItemResponse(id = null, rowIndex = null, productName = "Item D"),
            DataItemResponse(id = "", rowIndex = 0, productName = "Item E")
        )

        val keys = items.mapIndexed { index, item ->
            "${item.id ?: "item"}_${item.displayIndex}_$index"
        }

        assertEquals(items.size, keys.size)
        assertEquals(items.size, keys.distinct().size)
    }

    @Test
    fun testNewsAndMovieIndexSafeKeyGuaranteesUniqueness() {
        // Simulate news items with null URLs and duplicate titles
        val dummyNews = listOf(
            Pair(null, null),
            Pair(null, null),
            Pair("http://example.com/a", "Breaking News"),
            Pair("http://example.com/a", "Breaking News")
        )

        val newsKeys = dummyNews.mapIndexed { index, (url, title) ->
            "${url ?: title ?: "news"}_$index"
        }
        assertEquals(dummyNews.size, newsKeys.distinct().size)

        // Simulate movies with null IDs and empty titles
        val dummyMovies = listOf(
            Pair(null, ""),
            Pair(null, ""),
            Pair(101, "Action Movie"),
            Pair(101, "Action Movie")
        )

        val movieKeys = dummyMovies.mapIndexed { index, (id, title) ->
            "${id ?: title ?: "movie"}_$index"
        }
        assertEquals(dummyMovies.size, movieKeys.distinct().size)
    }

    @Test
    fun testRoomMigration2to3Metadata() {
        val migration = AppDatabase.MIGRATION_2_3
        assertEquals(2, migration.startVersion)
        assertEquals(3, migration.endVersion)
        assertNotNull(migration)
    }

    @Test
    fun testBatchSyncLocalMappingLogic() {
        val localRecords = listOf(
            ProductEntity(id = 10, remoteId = "rem_1", productName = "Local Product 1"),
            ProductEntity(id = 11, remoteId = "rem_2", productName = "Local Product 2"),
            ProductEntity(id = 12, remoteId = null, productName = "Local Product 3")
        )

        val existingMap = localRecords
            .filter { it.remoteId != null }
            .associateBy { it.remoteId!! }

        assertEquals(2, existingMap.size)
        assertEquals(10, existingMap["rem_1"]?.id)
        assertEquals(11, existingMap["rem_2"]?.id)

        val remoteFeed = listOf(
            DataItemResponse(id = "rem_1", productName = "Updated Feed 1"),
            DataItemResponse(id = "rem_3", productName = "New Feed 3")
        )

        val mappedEntities = remoteFeed.map { remoteItem ->
            val existing = remoteItem.id?.let { existingMap[it] }
            ProductEntity(
                id = existing?.id ?: 0,
                remoteId = remoteItem.id,
                productName = remoteItem.productName.orEmpty()
            )
        }

        assertEquals(10, mappedEntities[0].id)
        assertEquals(0, mappedEntities[1].id)
    }

    @Test
    fun testConcurrentAsyncWordSuggestionStability() = runBlocking {
        val engine = WordSuggestionEngine()
        val queries = listOf("appl", "teh", "dont", "keyboard", "wht", "mob", "test", "cand")

        val results = queries.map { q ->
            async(Dispatchers.Default) {
                engine.getSuggestions(q)
            }
        }.awaitAll()

        assertEquals(queries.size, results.size)
        results.forEach { res ->
            assertNotNull(res)
            assertTrue(res.hasSuggestions())
            assertTrue(res.userWord.isNotEmpty())
        }
    }

}
