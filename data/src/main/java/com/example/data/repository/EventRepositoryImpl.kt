package com.example.data.repository

import com.example.core.domain.Event
import com.example.core.domain.EventRepository
import com.example.core.domain.TicketType
import com.example.data.dto.EventDto
import com.example.data.dto.TicketTypeDto
import com.example.data.remote.EventApi
import com.example.data.util.runCatchingApi


class EventRepositoryImpl(
    private val eventApi: EventApi
) : EventRepository {

    override suspend fun getEvents(): Result<List<Event>> = runCatchingApi {
        eventApi.getEvents().map { it.toDomain() }
    }
}

private fun EventDto.toDomain() = Event(
    id = id,
    name = name,
    description = description,
    venue = venue,
    startsAt = startsAt,
    endsAt = endsAt,
    ticketTypes = ticketTypes.map { it.toDomain() }
)

private fun TicketTypeDto.toDomain() = TicketType(
    id = id,
    name = name,
    priceCents = priceCents,
    capacity = capacity,
    soldCount = soldCount,
    remaining = remaining
)