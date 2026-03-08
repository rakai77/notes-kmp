package com.example.notes_kmp.presentation.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.notes_kmp.common.BaseResult
import com.example.notes_kmp.domain.usecase.GetTopHeadlineUseCase
import com.example.notes_kmp.presentation.screen.component.NewsCategory
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class HomeViewModel(
    private val useCase: GetTopHeadlineUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    init {
        loadTopHeadlines()
    }

    fun onCategorySelected(category: NewsCategory) {
        if (_state.value.selectedCategory == category) return
        _state.update { it.copy(selectedCategory = category) }
        loadTopHeadlines()
    }

    fun loadTopHeadlines() {
        val category = _state.value.selectedCategory

        viewModelScope.launch {
            useCase(
                country = "us",
                category = category.value,
                pageSize = 100,
                page = 1
            )
                .onStart {
                    _state.update { it.copy(isLoading = true, error = null) }
                }
                .onCompletion {
                    _state.update { it.copy(isLoading = false) }
                }
                .collect { result ->
                    when (result) {
                        is BaseResult.Success -> {
                            _state.update {
                                it.copy(
                                    articles = result.data.articles,
                                    isLoading = false,
                                )
                            }
                        }
                        is BaseResult.Error.HttpError -> {
                            _state.update {
                                it.copy(isLoading = false, error = result.message)
                            }
                        }
                        is BaseResult.Error.NetworkError -> {
                            _state.update {
                                it.copy(isLoading = false, error = result.throwable.message ?: "Network error")
                            }
                        }
                        is BaseResult.Error.UnknownError -> {
                            _state.update {
                                it.copy(isLoading = false, error = "Unknown error")
                            }
                        }
                    }
                }
        }
    }
}
