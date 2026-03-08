package com.example.notes_kmp.domain.usecase

import app.cash.turbine.test
import com.example.notes_kmp.common.BaseResult
import com.example.notes_kmp.data.fake.FakeNewsRepository
import com.example.notes_kmp.domain.model.TopHeadline
import com.example.notes_kmp.domain.usecase.GetSearchNewsUseCase
import kotlinx.coroutines.test.runTest
import kotlin.test.AfterTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class GetSearchNewsUseCaseTest {

    private val fakeRepository = FakeNewsRepository()
    private val useCase = GetSearchNewsUseCase(fakeRepository)

    @AfterTest
    fun tearDown() = fakeRepository.reset()

    @Test
    fun `invoke emits Success with articles`() = runTest {
        val articles = listOf(
            fakeRepository.buildArticle("https://example.com/1"),
            fakeRepository.buildArticle("https://example.com/2"),
            fakeRepository.buildArticle("https://example.com/3"),
        )
        fakeRepository.searchResult = BaseResult.Success(
            fakeRepository.buildTopHeadline(articles)
        )

        useCase(query = "kotlin").test {
            val result = awaitItem()
            assertIs<BaseResult.Success<TopHeadline>>(result)
            assertEquals(3, result.data.articles.size)
            awaitComplete()
        }
    }

    @Test
    fun `invoke passes query correctly`() = runTest {
        useCase(query = "kotlin multiplatform").test {
            awaitItem()
            awaitComplete()
        }

        assertEquals("kotlin multiplatform", fakeRepository.capturedQuery)
    }

    @Test
    fun `invoke passes sortBy popularity by default`() = runTest {
        useCase(query = "news").test {
            awaitItem()
            awaitComplete()
        }

        assertEquals("popularity", fakeRepository.capturedSortBy)
    }

    @Test
    fun `invoke passes custom sortBy correctly`() = runTest {
        useCase(query = "news", sortBy = "publishedAt").test {
            awaitItem()
            awaitComplete()
        }

        assertEquals("publishedAt", fakeRepository.capturedSortBy)
    }

    @Test
    fun `invoke emits Success with empty articles when no results`() = runTest {
        fakeRepository.searchResult = BaseResult.Success(
            fakeRepository.buildTopHeadline(emptyList())
        )

        useCase(query = "xyznotfound").test {
            val result = awaitItem()
            assertIs<BaseResult.Success<TopHeadline>>(result)
            assertTrue(result.data.articles.isEmpty())
            awaitComplete()
        }
    }

    @Test
    fun `invoke emits NetworkError from repository`() = runTest {
        fakeRepository.searchResult = BaseResult.Error.NetworkError(
            throwable = RuntimeException("Timeout")
        )

        useCase(query = "kotlin").test {
            val result = awaitItem()
            assertIs<BaseResult.Error.NetworkError>(result)
            assertEquals("Timeout", result.throwable.message)
            awaitComplete()
        }
    }
}
