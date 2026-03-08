package com.example.notes_kmp.data.fake

import com.example.notes_kmp.common.BaseResult
import com.example.notes_kmp.domain.model.Article
import com.example.notes_kmp.domain.model.TopHeadline
import com.example.notes_kmp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf


class FakeNewsRepository : NewsRepository {

    var capturedCountry: String? = null
    var capturedCategory: String? = null
    var capturedSources: String? = null
    var capturedQuery: String? = null
    var capturedPageSize: Int = 0
    var capturedPage: Int = 0
    var capturedSortBy: String? = null

    var topHeadlinesResult: BaseResult<TopHeadline> = BaseResult.Success(emptyTopHeadline())
    var searchResult: BaseResult<TopHeadline> = BaseResult.Success(emptyTopHeadline())

    override suspend fun getTopHeadlines(
        country: String?,
        category: String?,
        sources: String?,
        query: String?,
        pageSize: Int,
        page: Int
    ): Flow<BaseResult<TopHeadline>> {
        capturedCountry = country
        capturedCategory = category
        capturedSources = sources
        capturedQuery = query
        capturedPageSize = pageSize
        capturedPage = page
        return flowOf(topHeadlinesResult)
    }

    override suspend fun searchNews(
        query: String,
        sortBy: String,
    ): Flow<BaseResult<TopHeadline>> {
        capturedQuery = query
        capturedSortBy = sortBy
        return flowOf(searchResult)
    }

    fun reset() {
        capturedCountry = null
        capturedCategory = null
        capturedSources = null
        capturedQuery = null
        capturedPageSize = 0
        capturedPage = 0
        capturedSortBy = null
        topHeadlinesResult = BaseResult.Success(emptyTopHeadline())
        searchResult = BaseResult.Success(emptyTopHeadline())
    }

    // ─── Helpers ────────────────────────────────────────────────────────────────

    private fun emptyTopHeadline() = TopHeadline(
        status = "ok",
        totalResults = 0,
        articles = emptyList(),
        code = null,
        message = null
    )

    fun buildTopHeadline(articles: List<Article> = emptyList()) = TopHeadline(
        status = "ok",
        totalResults = articles.size,
        articles = articles,
        code = null,
        message = null
    )

    fun buildArticle(url: String = "https://example.com") = Article(
        sourceId = "src",
        sourceName = "Source",
        author = "Author",
        title = "Title",
        description = "Description",
        url = url,
        urlToImage = null,
        publishedAt = "2026-03-08T00:00:00Z",
        content = null
    )
}
