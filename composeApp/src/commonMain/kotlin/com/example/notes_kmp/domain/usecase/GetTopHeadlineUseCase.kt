package com.example.notes_kmp.domain.usecase

import com.example.notes_kmp.common.BaseResult
import com.example.notes_kmp.domain.model.TopHeadline
import com.example.notes_kmp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow

class GetTopHeadlineUseCase(
    private val newsRepository: NewsRepository
) {
    suspend operator fun invoke(
        country: String? = null,
        category: String? = null,
        sources: String? = null,
        query: String? = null,
        pageSize: Int = 20,
        page: Int = 1
    ) : Flow<BaseResult<TopHeadline>> {
        return newsRepository.getTopHeadlines(country, category, sources, query, pageSize, page)
    }
}
