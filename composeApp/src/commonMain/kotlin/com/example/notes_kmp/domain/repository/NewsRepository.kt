package com.example.notes_kmp.domain.repository

import com.example.notes_kmp.common.BaseResult
import com.example.notes_kmp.domain.model.TopHeadline
import kotlinx.coroutines.flow.Flow

interface NewsRepository {
    suspend fun getTopHeadlines(
        country: String? = null,
        category: String? = null,
        sources: String? = null,
        query: String? = null,
        pageSize: Int = 10,
        page: Int = 1
    ): Flow<BaseResult<TopHeadline>>
}