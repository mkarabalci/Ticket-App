package com.example.data.repository

import com.example.core.domain.event.Event
import com.example.core.domain.event.EventRepository
import com.example.core.domain.event.TicketType
import com.example.data.dto.event.EventDto
import com.example.data.dto.event.TicketTypeDto
import com.example.data.mapper.toDomain
import com.example.data.remote.EventApi
import com.example.data.util.runCatchingApi



class EventRepositoryImpl(
    private val eventApi: EventApi
) : EventRepository {
    override suspend fun getEvents(): Result<List<Event>> = runCatchingApi { eventApi.getEvents() }.map { list -> list.map { it.toDomain() }}

    override suspend fun getEvent(id: String): Result<Event> = runCatchingApi { eventApi.getEvent(id) }.map { it.toDomain() }
}
