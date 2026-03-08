package com.example.notes_kmp.presentation.screen.home

import com.example.notes_kmp.domain.model.Article
import com.example.notes_kmp.presentation.screen.component.NewsCategory

data class HomeUiState (
    val articles: List<Article> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val selectedCategory: NewsCategory = NewsCategory.GENERAL
)