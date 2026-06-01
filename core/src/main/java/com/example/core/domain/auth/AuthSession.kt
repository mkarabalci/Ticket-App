package com.example.core.domain.auth

import com.example.core.domain.auth.User

data class AuthSession(val user: User, val accessToken: String, val refreshToken: String) {}