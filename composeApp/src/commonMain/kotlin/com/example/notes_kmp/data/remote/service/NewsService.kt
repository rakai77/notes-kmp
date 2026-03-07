package com.example.notes_kmp.data.remote.service

import com.example.notes_kmp.data.remote.response.TopHeadlinesResponse

interface NewsService {
    suspend fun getTopHeadlines(
        country: String? = null,
        category: String? = null,
        sources: String? = null,
        query: String? = null,
        pageSize: Int = 10,
        page: Int = 1
    ): TopHeadlinesResponse
}