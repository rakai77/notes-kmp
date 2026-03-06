package com.example.notes_kmp

import androidx.compose.ui.window.ComposeUIViewController
import com.example.notes_kmp.di.initKoinModule
import com.example.notes_kmp.presentation.screen.NotesApp

fun MainViewController() = ComposeUIViewController(
    configure = { initKoinModule(additionalModules = listOf()) }
) {
    NotesApp()
}