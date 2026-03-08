package com.example.notes_kmp.data.mapper

import com.example.notes_kmp.data.remote.response.ArticleResponse
import com.example.notes_kmp.data.remote.response.SourceResponse
import com.example.notes_kmp.data.remote.response.TopHeadlinesResponse
import com.example.notes_kmp.data.remote.response.toDomain
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull
import kotlin.test.assertTrue

class NewsMapperTest {

    @Test
    fun `toDomain maps totalResults correctly`() {
        val response = buildResponse(totalResults = 34, articles = emptyList())
        assertEquals(34, response.toDomain().totalResults)
    }

    @Test
    fun `toDomain maps status correctly`() {
        val response = buildResponse(status = "ok", articles = emptyList())
        assertEquals("ok", response.toDomain().status)
    }

    @Test
    fun `toDomain maps all article fields correctly`() {
        val response = buildResponse(
            articles = listOf(
                buildArticleResponse(
                    sourceId = "bbc",
                    sourceName = "BBC News",
                    author = "John Doe",
                    title = "Breaking News",
                    description = "Something happened",
                    url = "https://bbc.com/1",
                    urlToImage = "https://bbc.com/image.jpg",
                    publishedAt = "2026-03-08T10:00:00Z",
                    content = "Full content"
                )
            )
        )

        val article = response.toDomain().articles.first()

        // flat fields sesuai Article domain model
        assertEquals("bbc", article.sourceId)
        assertEquals("BBC News", article.sourceName)
        assertEquals("John Doe", article.author)
        assertEquals("Breaking News", article.title)
        assertEquals("Something happened", article.description)
        assertEquals("https://bbc.com/1", article.url)
        assertEquals("https://bbc.com/image.jpg", article.urlToImage)
        assertEquals("2026-03-08T10:00:00Z", article.publishedAt)
        assertEquals("Full content", article.content)
    }

    @Test
    fun `toDomain maps multiple articles correctly`() {
        val response = buildResponse(
            articles = listOf(
                buildArticleResponse(url = "https://example.com/1"),
                buildArticleResponse(url = "https://example.com/2"),
                buildArticleResponse(url = "https://example.com/3"),
            )
        )

        assertEquals(3, response.toDomain().articles.size)
    }

    @Test
    fun `toDomain converts null title to empty string`() {
        val response = buildResponse(articles = listOf(buildArticleResponse(title = null)))
        assertEquals("", response.toDomain().articles.first().title)
    }

    @Test
    fun `toDomain converts null url to empty string`() {
        val response = buildResponse(articles = listOf(buildArticleResponse(url = null)))
        assertEquals("", response.toDomain().articles.first().url)
    }

    @Test
    fun `toDomain converts null sourceName to empty string`() {
        val response = buildResponse(articles = listOf(buildArticleResponse(sourceName = null)))
        assertEquals("", response.toDomain().articles.first().sourceName)
    }

    @Test
    fun `toDomain converts null publishedAt to empty string`() {
        val response = buildResponse(articles = listOf(buildArticleResponse(publishedAt = null)))
        assertEquals("", response.toDomain().articles.first().publishedAt)
    }

    @Test
    fun `toDomain keeps nullable fields as null`() {
        val response = buildResponse(
            articles = listOf(
                buildArticleResponse(
                    sourceId = null,
                    author = null,
                    description = null,
                    urlToImage = null,
                    content = null
                )
            )
        )

        val article = response.toDomain().articles.first()

        assertNull(article.sourceId)
        assertNull(article.author)
        assertNull(article.description)
        assertNull(article.urlToImage)
        assertNull(article.content)
    }

    @Test
    fun `toDomain handles null source object`() {
        val response = buildResponse(
            articles = listOf(
                ArticleResponse(source = null) // source null sepenuhnya
            )
        )

        val article = response.toDomain().articles.first()

        assertNull(article.sourceId)
        assertEquals("", article.sourceName)
    }

    @Test
    fun `toDomain returns empty list when articles is empty`() {
        val result = buildResponse(articles = emptyList()).toDomain()
        assertTrue(result.articles.isEmpty())
    }

    // ─── Helpers ────────────────────────────────────────────────────────────────

    private fun buildResponse(
        status: String = "ok",
        totalResults: Int = 0,
        articles: List<ArticleResponse>
    ) = TopHeadlinesResponse(
        status = status,
        totalResults = totalResults,
        articles = articles,
        code = null,
        message = null
    )

    private fun buildArticleResponse(
        sourceId: String? = "test-id",
        sourceName: String? = "Test Source",
        author: String? = "Test Author",
        title: String? = "Test Title",
        description: String? = "Test Description",
        url: String? = "https://example.com",
        urlToImage: String? = "https://example.com/image.jpg",
        publishedAt: String? = "2026-03-08T00:00:00Z",
        content: String? = "Test content"
    ) = ArticleResponse(
        source = SourceResponse(id = sourceId, name = sourceName),
        author = author,
        title = title,
        description = description,
        url = url,
        urlToImage = urlToImage,
        publishedAt = publishedAt,
        content = content
    )
}
