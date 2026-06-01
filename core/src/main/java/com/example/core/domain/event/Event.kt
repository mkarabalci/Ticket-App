package com.example.core.domain.event

import com.example.core.domain.event.TicketType

data class Event(
    val id: String,
    val name: String,
    val description: String,
    val venue: String,
    val startsAt: String,
    val endsAt: String,
    val ticketTypes: List<TicketType>
)