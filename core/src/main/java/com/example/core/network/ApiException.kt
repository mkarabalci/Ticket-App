package com.example.core.network

class ApiException(
    val code: Int,
    val errorMessage: String?,
    cause: Throwable? = null
) : RuntimeException("HTTP $code: $errorMessage", cause)