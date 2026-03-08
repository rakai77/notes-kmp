package com.example.notes_kmp.domain.repository

import app.cash.turbine.test
import com.example.notes_kmp.common.BaseResult
import com.example.notes_kmp.data.fake.FakeNewsService
import com.example.notes_kmp.data.remote.response.ArticleResponse
import com.example.notes_kmp.data.remote.response.SourceResponse
import com.example.notes_kmp.data.remote.response.TopHeadlinesResponse
import com.example.notes_kmp.domain.model.TopHeadline
import com.example.notes_kmp.domain.repository.NewsRepositoryImpl
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class NewsRepositoryTest {

    private val fakeService = FakeNewsService()
    private val testDispatcher = UnconfinedTestDispatcher()
    private val repository = NewsRepositoryImpl(
        newsService = fakeService,
        context = testDispatcher
    )

    @AfterTest
    fun tearDown() {
        fakeService.reset()
    }

    // ─── getTopHeadlines: Success ───────────────────────────────────────────────

    @Test
    fun `getTopHeadlines emits Success when status is ok`() = runTest {
        fakeService.topHeadlinesResponse = buildResponse(
            articles = listOf(buildArticleResponse("url1"), buildArticleResponse("url2"))
        )

        repository.getTopHeadlines(
            country = "us", category = "general",
            sources = null, query = null,
            pageSize = 10, page = 1
        ).test {
            val result = awaitItem()
            assertIs<BaseResult.Success<TopHeadline>>(result)
            assertEquals(2, result.data.articles.size)
            awaitComplete()
        }
    }

    @Test
    fun `getTopHeadlines maps articles correctly on success`() = runTest {
        fakeService.topHeadlinesResponse = buildResponse(
            articles = listOf(buildArticleResponse("https://bbc.com/1"))
        )

        repository.getTopHeadlines(
            country = "us", category = null,
            sources = null, query = null,
            pageSize = 10, page = 1
        ).test {
            val result = awaitItem() as BaseResult.Success
            assertEquals("https://bbc.com/1", result.data.articles.first().url)
            awaitComplete()
        }
    }

    // ─── getTopHeadlines: Error ─────────────────────────────────────────────────

    @Test
    fun `getTopHeadlines emits HttpError when status is not ok`() = runTest {
        fakeService.topHeadlinesResponse = TopHeadlinesResponse(
            status = "error",
            totalResults = 0,
            articles = emptyList(),
            code = "apiKeyInvalid",
            message = "Your API key is invalid"
        )

        repository.getTopHeadlines(
            country = "us", category = null,
            sources = null, query = null,
            pageSize = 10, page = 1
        ).test {
            val result = awaitItem()
            assertIs<BaseResult.Error.HttpError>(result)
            assertEquals("Your API key is invalid", result.message)
            awaitComplete()
        }
    }

    @Test
    fun `getTopHeadlines emits NetworkError on exception`() = runTest {
        fakeService.throwable = RuntimeException("No internet")

        repository.getTopHeadlines(
            country = "us", category = null,
            sources = null, query = null,
            pageSize = 10, page = 1
        ).test {
            val result = awaitItem()
            assertIs<BaseResult.Error.NetworkError>(result)
            assertEquals("No internet", result.throwable.message)
            awaitComplete()
        }
    }

    // ─── searchNews ─────────────────────────────────────────────────────────────

    @Test
    fun `searchNews emits Success with correct articles`() = runTest {
        fakeService.everythingResponse = buildResponse(
            articles = listOf(
                buildArticleResponse("https://example.com/1"),
                buildArticleResponse("https://example.com/2"),
            )
        )

        repository.searchNews(
            query = "kotlin",
            sortBy = "popularity"
        ).test {
            val result = awaitItem()
            assertIs<BaseResult.Success<TopHeadline>>(result)
            assertEquals(2, result.data.articles.size)
            awaitComplete()
        }
    }

    @Test
    fun `searchNews emits NetworkError on exception`() = runTest {
        fakeService.throwable = RuntimeException("Timeout")

        repository.searchNews(
            query = "kotlin",
            sortBy = "popularity"
        ).test {
            val result = awaitItem()
            assertIs<BaseResult.Error.NetworkError>(result)
            awaitComplete()
        }
    }

    @Test
    fun `searchNews emits Success with empty articles`() = runTest {
        fakeService.everythingResponse = buildResponse(articles = emptyList())

        repository.searchNews(
            query = "xyzabc123notfound",
            sortBy = "popularity",
        ).test {
            val result = awaitItem()
            assertIs<BaseResult.Success<TopHeadline>>(result)
            assertTrue(result.data.articles.isEmpty())
            awaitComplete()
        }
    }

    // ─── Helpers ────────────────────────────────────────────────────────────────

    private fun buildResponse(
        status: String = "ok",
        articles: List<ArticleResponse>
    ) = TopHeadlinesResponse(
        status = status,
        totalResults = articles.size,
        articles = articles,
        code = null,
        message = null
    )

    private fun buildArticleResponse(url: String = "https://example.com") = ArticleResponse(
        source = SourceResponse(id = "src", name = "Source"),
        author = "Author",
        title = "Title",
        description = "Description",
        url = url,
        urlToImage = null,
        publishedAt = "2026-03-08T00:00:00Z",
        content = null
    )
}
