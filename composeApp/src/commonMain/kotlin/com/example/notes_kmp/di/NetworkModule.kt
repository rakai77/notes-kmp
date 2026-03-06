package com.example.notes_kmp.di

import com.example.notes_kmp.data.remote.createJsonParser
import com.example.notes_kmp.data.remote.setupHttpClient
import com.example.notes_kmp.getPlatform
import com.example.notes_kmp.utils.Constants
import org.koin.dsl.module

val networkModule = module {
    single { createJsonParser() }

    single {
        setupHttpClient(
            baseUrl = Constants.BASE_URL,
            isDebugMode = true,
            httpClientProvider = getPlatform().getHttpClient(false)
        )
    }
}