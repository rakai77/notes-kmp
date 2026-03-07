package com.example.notes_kmp.di

import com.example.notes_kmp.coroutineContext
import com.example.notes_kmp.data.remote.service.NewsService
import com.example.notes_kmp.data.remote.service.NewsServiceImpl
import com.example.notes_kmp.data.remote.setupHttpClient
import com.example.notes_kmp.domain.repository.NewsRepository
import com.example.notes_kmp.domain.repository.NewsRepositoryImpl
import com.example.notes_kmp.domain.usecase.GetTopHeadlineUseCase
import com.example.notes_kmp.presentation.screen.home.HomeViewModel
import com.example.notes_kmp.provideHttpClientEngine
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module
import kotlin.coroutines.CoroutineContext

val networkModule = module {

    single { provideHttpClientEngine() }

    single { setupHttpClient(get()) }

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

    viewModelOf(::HomeViewModel)
}