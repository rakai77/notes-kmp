package com.example.notes_kmp.presentation

import com.example.notes_kmp.common.BaseResult
import com.example.notes_kmp.common.NewsApiErrorCode
import com.example.notes_kmp.data.fake.FakeNewsRepository
import com.example.notes_kmp.domain.usecase.GetSearchNewsUseCase
import com.example.notes_kmp.domain.usecase.GetTopHeadlineUseCase
import com.example.notes_kmp.presentation.screen.component.NewsCategory
import com.example.notes_kmp.presentation.screen.home.HomeViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

@OptIn(ExperimentalCoroutinesApi::class)
class HomeViewModelTest {

    private val testDispatcher = StandardTestDispatcher()
    private val fakeRepository = FakeNewsRepository()
    private val useCase = GetTopHeadlineUseCase(fakeRepository)
    private val searchUseCase = GetSearchNewsUseCase(fakeRepository)


    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain()
        fakeRepository.reset()
    }

    private fun buildViewModel() = HomeViewModel(useCase, searchUseCase)

    // ─── init ──────────────────────────────────────────────────────────────────

    @Test
    fun `init loads top headlines on start`() = runTest {
        val articles = listOf(
            fakeRepository.buildArticle("https://example.com/1"),
            fakeRepository.buildArticle("https://example.com/2")
        )
        fakeRepository.topHeadlinesResult = BaseResult.Success(
            fakeRepository.buildTopHeadline(articles)
        )

        val viewModel = buildViewModel()
        advanceUntilIdle()

        assertEquals(2, viewModel.state.value.articles.size)
        assertFalse(viewModel.state.value.isLoading)
        assertNull(viewModel.state.value.error)
    }

//    @Test
//    fun `init sets isLoading true then false`() = runTest {
//        val viewModel = buildViewModel()
//
//        viewModel.state.test {
//            val loading = awaitItem()
//            assertTrue(loading.isLoading)
//
//            advanceUntilIdle()
//
//            val done = awaitItem()
//            assertFalse(done.isLoading)
//
//            cancelAndIgnoreRemainingEvents()
//        }
//    }

    // ─── loadTopHeadlines ──────────────────────────────────────────────────────

    @Test
    fun `loadTopHeadlines emits error on HttpError`() = runTest {
        fakeRepository.topHeadlinesResult = BaseResult.Error.HttpError(
            httpCode = 401,
            apiCode = NewsApiErrorCode.API_KEY_INVALID,
            message = "Invalid API key"
        )

        val viewModel = buildViewModel()
        advanceUntilIdle()

        assertEquals("Invalid API key", viewModel.state.value.error)
        assertFalse(viewModel.state.value.isLoading)
    }

    @Test
    fun `loadTopHeadlines emits error on NetworkError`() = runTest {
        fakeRepository.topHeadlinesResult = BaseResult.Error.NetworkError(
            throwable = RuntimeException("No internet")
        )

        val viewModel = buildViewModel()
        advanceUntilIdle()

        assertEquals("No internet", viewModel.state.value.error)
    }

    @Test
    fun `loadTopHeadlines clears error on retry`() = runTest {
        fakeRepository.topHeadlinesResult = BaseResult.Error.NetworkError(
            throwable = RuntimeException("No internet")
        )
        val viewModel = buildViewModel()
        advanceUntilIdle()
        assertEquals("No internet", viewModel.state.value.error)

        fakeRepository.topHeadlinesResult = BaseResult.Success(
            fakeRepository.buildTopHeadline(listOf(fakeRepository.buildArticle()))
        )
        viewModel.loadTopHeadlines()
        advanceUntilIdle()

        assertNull(viewModel.state.value.error)
        assertEquals(1, viewModel.state.value.articles.size)
    }

    // ─── onCategorySelected ────────────────────────────────────────────────────

    @Test
    fun `onCategorySelected updates selectedCategory`() = runTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onCategorySelected(NewsCategory.TECHNOLOGY)
        advanceUntilIdle()

        assertEquals(NewsCategory.TECHNOLOGY, viewModel.state.value.selectedCategory)
    }

    @Test
    fun `onCategorySelected passes correct category to repository`() = runTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onCategorySelected(NewsCategory.SPORTS)
        advanceUntilIdle()

        assertEquals("sports", fakeRepository.capturedCategory)
    }

    @Test
    fun `onCategorySelected clears search query and isSearchActive`() = runTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onSearchQueryChange("kotlin")
        advanceTimeBy(600) // lewati debounce
        advanceUntilIdle()

        viewModel.onCategorySelected(NewsCategory.BUSINESS)
        advanceUntilIdle()

        assertEquals("", viewModel.state.value.searchQuery)
        assertFalse(viewModel.state.value.isSearchActive)
    }

    @Test
    fun `onCategorySelected does nothing when same category selected`() = runTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()

        val initialCategory = viewModel.state.value.selectedCategory
        fakeRepository.reset()

        viewModel.onCategorySelected(initialCategory)
        advanceUntilIdle()

        assertNull(fakeRepository.capturedCategory)
    }

    // ─── onSearchQueryChange (debounce) ────────────────────────────────────────

    @Test
    fun `onSearchQueryChange updates searchQuery state`() = runTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onSearchQueryChange("kotlin")

        assertEquals("kotlin", viewModel.state.value.searchQuery)
    }

    @Test
    fun `onSearchQueryChange does not trigger search before 500ms`() = runTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()
        fakeRepository.reset()

        viewModel.onSearchQueryChange("kotlin")
        advanceTimeBy(499)


        assertNull(fakeRepository.capturedQuery)
        assertFalse(viewModel.state.value.isSearchActive)
    }

    @Test
    fun `onSearchQueryChange triggers search after 500ms`() = runTest {
        val articles = listOf(fakeRepository.buildArticle())
        fakeRepository.searchResult = BaseResult.Success(
            fakeRepository.buildTopHeadline(articles)
        )
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onSearchQueryChange("kotlin")
        advanceTimeBy(500)
        advanceUntilIdle()

        assertEquals("kotlin", fakeRepository.capturedQuery)
        assertTrue(viewModel.state.value.isSearchActive)
        assertEquals(1, viewModel.state.value.articles.size)
    }

    @Test
    fun `onSearchQueryChange cancels previous debounce on rapid typing`() = runTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()
        fakeRepository.reset()

        // rapid typing
        viewModel.onSearchQueryChange("k")
        advanceTimeBy(100)
        viewModel.onSearchQueryChange("ko")
        advanceTimeBy(100)
        viewModel.onSearchQueryChange("kot")
        advanceTimeBy(100)
        viewModel.onSearchQueryChange("kotl")
        advanceTimeBy(500)
        advanceUntilIdle()

        assertEquals("kotl", fakeRepository.capturedQuery)
    }

    @Test
    fun `onSearchQueryChange with blank query resets to top headlines`() = runTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onSearchQueryChange("kotlin")
        advanceTimeBy(600)
        advanceUntilIdle()
        assertTrue(viewModel.state.value.isSearchActive)

        viewModel.onSearchQueryChange("")
        advanceUntilIdle()

        assertFalse(viewModel.state.value.isSearchActive)
        assertEquals("", viewModel.state.value.searchQuery)
    }

    // ─── onSearch (immediate) ──────────────────────────────────────────────────

    @Test
    fun `onSearch triggers search immediately without delay`() = runTest {
        val articles = listOf(fakeRepository.buildArticle())
        fakeRepository.searchResult = BaseResult.Success(
            fakeRepository.buildTopHeadline(articles)
        )
        val viewModel = buildViewModel()
        advanceUntilIdle()

        viewModel.onSearchQueryChange("kotlin")
        viewModel.onSearch()
        advanceUntilIdle()

        assertEquals("kotlin", fakeRepository.capturedQuery)
        assertTrue(viewModel.state.value.isSearchActive)
    }

    @Test
    fun `onSearch does nothing when query is blank`() = runTest {
        val viewModel = buildViewModel()
        advanceUntilIdle()
        fakeRepository.reset()

        viewModel.onSearch()
        advanceUntilIdle()

        assertNull(fakeRepository.capturedQuery)
        assertFalse(viewModel.state.value.isSearchActive)
    }
}
