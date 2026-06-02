package com.example.core.network

class NetworkException(cause: Throwable) : RuntimeException("Network Error", cause)