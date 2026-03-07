package com.example.notes_kmp.common

sealed class BaseResult<out T> {
    data class Success<T>(val data :T) : BaseResult<T>()
    sealed class Error : BaseResult<Nothing>() {
        data class HttpError(
            val httpCode: Int,
            val apiCode: NewsApiErrorCode,
            val message: String
        ) : Error()
        data class NetworkError(val throwable: Throwable) : Error()
        data object UnknownError : Error()
    }
}

enum class NewsApiErrorCode(val code: String) {
    API_KEY_DISABLED("apiKeyDisabled"),
    API_KEY_EXHAUSTED("apiKeyExhausted"),
    API_KEY_INVALID("apiKeyInvalid"),
    API_KEY_MISSING("apiKeyMissing"),
    PARAMETER_INVALID("parameterInvalid"),
    PARAMETERS_MISSING("parametersMissing"),
    RATE_LIMITED("rateLimited"),
    SOURCES_TOO_MANY("sourcesTooMany"),
    SOURCE_DOES_NOT_EXIST("sourceDoesNotExist"),
    UNEXPECTED_ERROR("unexpectedError"),
    UNKNOWN("unknown");

    companion object {
        fun fromCode(code: String): NewsApiErrorCode =
            entries.firstOrNull { it.code == code } ?: UNKNOWN
    }
}
