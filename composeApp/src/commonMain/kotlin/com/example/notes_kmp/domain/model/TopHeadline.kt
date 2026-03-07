package com.example.notes_kmp.domain.model

data class TopHeadline(
    val status: String,
    val totalResults: Int = 0,
    val articles: List<Article> = emptyList(),
    val code: String? = null,
    val message: String? = null
)