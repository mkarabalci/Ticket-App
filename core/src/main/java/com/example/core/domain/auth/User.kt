package com.example.core.domain.auth

import com.example.core.domain.auth.UserRole

data class User(val id: String, val email:String, val role: UserRole) {}