package com.example.notes_kmp.data.remote.response

import com.example.notes_kmp.domain.model.Article
import com.example.notes_kmp.domain.model.TopHeadline
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TopHeadlinesResponse(
    @SerialName("status") val status: String,
    @SerialName("totalResults") val totalResults: Int = 0,
    @SerialName("articles") val articles: List<ArticleResponse> = emptyList(),
    // Error fields
    @SerialName("code") val code: String? = null,
    @SerialName("message") val message: String? = null
)

@Serializable
data class ArticleResponse(
    @SerialName("source") val source: SourceResponse? = null,
    @SerialName("author") val author: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("url") val url: String? = null,
    @SerialName("urlToImage") val urlToImage: String? = null,
    @SerialName("publishedAt") val publishedAt: String? = null,
    @SerialName("content") val content: String? = null
)

@Serializable
data class SourceResponse(
    @SerialName("id") val id: String? = null,
    @SerialName("name") val name: String? = null
)

fun TopHeadlinesResponse.toDomain() = TopHeadline(
    status = status,
    totalResults = totalResults,
    articles = articles.map { it.toDomain() },
    code = code,
    message = message
)


fun ArticleResponse.toDomain() = Article(
    sourceId = source?.id,
    sourceName = source?.name.orEmpty(),
    author = author,
    title = title.orEmpty(),
    description = description,
    url = url.orEmpty(),
    urlToImage = urlToImage,
    publishedAt = publishedAt.orEmpty(),
    content = content
)