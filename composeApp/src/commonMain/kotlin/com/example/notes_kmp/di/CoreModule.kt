package com.example.notes_kmp.di

import com.example.notes_kmp.coroutineContext
import com.example.notes_kmp.data.remote.createJsonParser
import com.example.notes_kmp.data.remote.service.NewsService
import com.example.notes_kmp.data.remote.service.NewsServiceImpl
import com.example.notes_kmp.data.remote.setupHttpClient
import com.example.notes_kmp.domain.repository.NewsRepository
import com.example.notes_kmp.domain.repository.NewsRepositoryImpl
import com.example.notes_kmp.domain.usecase.GetTopHeadlineUseCase
import com.example.notes_kmp.getPlatform
import com.example.notes_kmp.utils.Constants
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext

val networkModule = module {
    single { createJsonParser() }

    single {
        setupHttpClient(
            baseUrl = Constants.BASE_URL,
            isDebugMode = true,
            httpClientProvider = getPlatform().getHttpClient(false)
        )
    }

    single { NewsServiceImpl(get()) } bind NewsService::class
}

val coreModule = module {
    single<CoroutineContext> { coroutineContext }

    single {
        NewsRepositoryImpl(
            newsService = get(),
            context = get()
        )
    } bind NewsRepository::class

    factory { GetTopHeadlineUseCase(get()) }
}