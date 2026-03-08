package com.example.notes_kmp.data.remote.service

import com.example.notes_kmp.data.remote.Endpoint
import com.example.notes_kmp.data.remote.response.TopHeadlinesResponse
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.parameter

class NewsServiceImpl(private val httpClient: HttpClient) : NewsService {
    override suspend fun getTopHeadlines(
        country: String?,
        category: String?,
        sources: String?,
        query: String?,
        pageSize: Int,
        page: Int
    ): TopHeadlinesResponse {
        return httpClient.get(Endpoint.TOP_HEADLINE) {
            country?.let { parameter("country", it) }
            category?.let { parameter("category", it) }
            sources?.let { parameter("sources", it) }
            query?.let { parameter("q", it) }
            parameter("pageSize", pageSize)
            parameter("page", page)
        }.body()
    }

    override suspend fun getEverything(
        query: String,
        sortBy: String
    ): TopHeadlinesResponse {
        return httpClient.get(Endpoint.EVERYTHING) {
            parameter("q", query)
            parameter("sortBy", sortBy)
            parameter("pageSize", "100")
            parameter("page", "1")
        }.body()
    }
}