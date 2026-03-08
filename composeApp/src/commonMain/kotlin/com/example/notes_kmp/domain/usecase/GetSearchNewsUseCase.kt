package com.example.notes_kmp.domain.usecase

import com.example.notes_kmp.common.BaseResult
import com.example.notes_kmp.domain.model.TopHeadline
import com.example.notes_kmp.domain.repository.NewsRepository
import kotlinx.coroutines.flow.Flow

class GetSearchNewsUseCase(
    private val repository: NewsRepository
) {
    suspend operator fun invoke(
        query: String,
        sortBy: String = "popularity",
    ): Flow<BaseResult<TopHeadline>> {
        return repository.searchNews(query, sortBy)
    }
}