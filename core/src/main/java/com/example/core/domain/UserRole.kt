package com.example.core.domain

enum class UserRole {
    USER, STAFF, ADMIN;

    //enum un içine fonk yazabilmek için
    companion object {
        // parser func.
        fun fromApi(value: String?): UserRole = when (value?.uppercase()) {
            "ADMIN" -> UserRole.ADMIN
            "STAFF" -> UserRole.STAFF
            else -> UserRole.USER
        }
    }
}