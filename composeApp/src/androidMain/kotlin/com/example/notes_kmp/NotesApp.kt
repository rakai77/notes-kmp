package com.example.notes_kmp

import android.app.Application
import com.example.notes_kmp.di.initKoinModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger

class NotesApp : Application() {
    override fun onCreate() {
        super.onCreate()
        initKoinModule(
            additionalModules = listOf(),
            appDeclaration = {
                androidLogger()
                androidContext(this@NotesApp)
            }
        )
    }
}