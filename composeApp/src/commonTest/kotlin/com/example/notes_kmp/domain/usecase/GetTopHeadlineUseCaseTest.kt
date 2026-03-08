package com.example.notes_kmp.domain.usecase

import app.cash.turbine.test
import com.example.notes_kmp.common.BaseResult
import com.example.notes_kmp.common.NewsApiErrorCode
import com.example.notes_kmp.data.fake.FakeNewsRepository
import com.example.notes_kmp.domain.model.TopHeadline
import com.example.notes_kmp.domain.usecase.GetTopHeadlineUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertNull

class GetTopHeadlineUseCaseTest {

    private val fakeRepository = FakeNewsRepository()
    private val useCase = GetTopHeadlineUseCase(fakeRepository)

    @AfterTest
    fun tearDown() = fakeRepository.reset()

    @Test
    fun `invoke emits Success from repository`() = runTest {
        val articles = listOf(
            fakeRepository.buildArticle("https://example.com/1"),
            fakeRepository.buildArticle("https://example.com/2")
        )
        fakeRepository.topHeadlinesResult = BaseResult.Success(
            fakeRepository.buildTopHeadline(articles)
        )

        useCase(country = "us", category = "general").test {
            val result = awaitItem()
            assertIs<BaseResult.Success<TopHeadline>>(result)
            assertEquals(2, result.data.articles.size)
            awaitComplete()
        }
    }

    @Test
    fun `invoke passes country and category correctly`() = runTest {
        useCase(country = "us", category = "technology").test {
            awaitItem()
            awaitComplete()
        }

        assertEquals("us", fakeRepository.capturedCountry)
        assertEquals("technology", fakeRepository.capturedCategory)
    }

    @Test
    fun `invoke passes pageSize and page correctly`() = runTest {
        useCase(country = "us", pageSize = 50, page = 2).test {
            awaitItem()
            awaitComplete()
        }

        assertEquals(50, fakeRepository.capturedPageSize)
        assertEquals(2, fakeRepository.capturedPage)
    }

    @Test
    fun `invoke uses default params when not specified`() = runTest {
        useCase().test {
            awaitItem()
            awaitComplete()
        }

        assertNull(fakeRepository.capturedCountry)
        assertNull(fakeRepository.capturedCategory)
        assertNull(fakeRepository.capturedQuery)
        assertEquals(20, fakeRepository.capturedPageSize)
        assertEquals(1, fakeRepository.capturedPage)
    }

    @Test
    fun `invoke emits HttpError from repository`() = runTest {
        fakeRepository.topHeadlinesResult = BaseResult.Error.HttpError(
            httpCode = 401,
            apiCode = NewsApiErrorCode.API_KEY_INVALID,
            message = "Invalid API key"
        )

        useCase(country = "us").test {
            val result = awaitItem()
            assertIs<BaseResult.Error.HttpError>(result)
            assertEquals(401, result.httpCode)
            assertEquals("Invalid API key", result.message)
            awaitComplete()
        }
    }

    @Test
    fun `invoke emits NetworkError from repository`() = runTest {
        fakeRepository.topHeadlinesResult = BaseResult.Error.NetworkError(
            throwable = RuntimeException("No internet")
        )

        useCase(country = "us").test {
            val result = awaitItem()
            assertIs<BaseResult.Error.NetworkError>(result)
            assertEquals("No internet", result.throwable.message)
            awaitComplete()
        }
    }
}
