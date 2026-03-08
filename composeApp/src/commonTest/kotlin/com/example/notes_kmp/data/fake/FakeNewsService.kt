package com.example.notes_kmp.data.fake

import com.example.notes_kmp.data.remote.response.TopHeadlinesResponse
import com.example.notes_kmp.data.remote.service.NewsService

class FakeNewsService: NewsService {
    var topHeadlinesResponse: TopHeadlinesResponse? = null
    var everythingResponse: TopHeadlinesResponse? = null
    var throwable: Throwable? = null

    override suspend fun getTopHeadlines(
        country: String?,
        category: String?,
        sources: String?,
        query: String?,
        pageSize: Int,
        page: Int
    ): TopHeadlinesResponse {
        throwable?.let { throw it }
        return topHeadlinesResponse ?: error("topHeadlinesResponse not set")
    }

    override suspend fun getEverything(
        query: String,
        sortBy: String
    ): TopHeadlinesResponse {
        throwable?.let { throw it }
        return everythingResponse ?: error("everythingResponse not set")
    }

    fun reset() {
        topHeadlinesResponse = null
        everythingResponse = null
        throwable = null
    }
}