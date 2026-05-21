package com.example.core.domain

interface EventRepository {
    suspend fun getEvents(): Result<List<Event>>
}