package com.example.notes_kmp.domain.repository

import com.example.notes_kmp.common.BaseResult
import com.example.notes_kmp.common.NewsApiErrorCode
import com.example.notes_kmp.data.remote.response.toDomain
import com.example.notes_kmp.data.remote.service.NewsService
import com.example.notes_kmp.domain.model.TopHeadline
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlin.coroutines.CoroutineContext

class NewsRepositoryImpl(
    private val newsService: NewsService,
    private val context: CoroutineContext
) : NewsRepository {
    override suspend fun getTopHeadlines(
        country: String?,
        category: String?,
        sources: String?,
        query: String?,
        pageSize: Int,
        page: Int
    ): Flow<BaseResult<TopHeadline>> = flow {
        try {
            val response = newsService.getTopHeadlines(country, category, sources, query, pageSize, page)
            if (response.status == "ok") {
                emit(BaseResult.Success(response.toDomain()))
            } else {
                emit(
                    BaseResult.Error.HttpError(
                        httpCode = 400,
                        apiCode = NewsApiErrorCode.fromCode(response.code.orEmpty()),
                        message = response.message.orEmpty()
                    )
                )
            }
        } catch (e: ClientRequestException) {
            emit(
                BaseResult.Error.HttpError(
                    httpCode = e.response.status.value,
                    apiCode = NewsApiErrorCode.fromCode(e.message),
                    message = e.message
                )
            )
        } catch (e: ServerResponseException) {
            emit(BaseResult.Error.NetworkError(e))
        } catch (e: Exception) {
            emit(BaseResult.Error.NetworkError(e))
        }
    }.flowOn(context)

    override suspend fun searchNews(
        query: String,
        sortBy: String
    ): Flow<BaseResult<TopHeadline>>  =  flow{
        try {
            val response = newsService.getEverything(query, sortBy)
            if (response.status == "ok") {
                emit(BaseResult.Success(response.toDomain()))
            } else {
                emit(
                    BaseResult.Error.HttpError(
                        httpCode = 400,
                        apiCode = NewsApiErrorCode.fromCode(response.code.orEmpty()),
                        message = response.message.orEmpty()
                    )
                )
            }
        } catch (e: ClientRequestException) {
            emit(
                BaseResult.Error.HttpError(
                    httpCode = e.response.status.value,
                    apiCode = NewsApiErrorCode.fromCode(e.message),
                    message = e.message
                )
            )
        } catch (e: ServerResponseException) {
            emit(BaseResult.Error.NetworkError(e))
        } catch (e: Exception) {
            emit(BaseResult.Error.NetworkError(e))
        }
    }.flowOn(context)
}