package com.example.data.repository

import com.example.core.domain.event.Event
import com.example.core.domain.event.EventRepository
import com.example.core.domain.event.TicketType
import com.example.data.dto.event.EventDto
import com.example.data.dto.event.TicketTypeDto
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
    venue = place,
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